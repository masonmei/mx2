// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.constructor;

import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.MappingNode;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.SequenceNode;
import java.util.LinkedList;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.util.HashMap;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import com.newrelic.agent.deps.org.yaml.snakeyaml.composer.Composer;
import java.util.Map;

public class BaseConstructor
{
    protected final Map<String, Construct> yamlConstructors;
    private Composer composer;
    private final Map<Node, Object> constructedObjects;
    private final Map<Node, Object> recursiveObjects;
    protected Class<?> rootType;
    
    public BaseConstructor() {
        this.yamlConstructors = new HashMap<String, Construct>();
        this.constructedObjects = new HashMap<Node, Object>();
        this.recursiveObjects = new HashMap<Node, Object>();
        this.rootType = Object.class;
    }
    
    public void setComposer(final Composer composer) {
        this.composer = composer;
    }
    
    public boolean checkData() {
        return this.composer.checkNode();
    }
    
    public Object getData() {
        this.composer.checkNode();
        final Node node = this.composer.getNode();
        node.setType(this.rootType);
        return this.constructDocument(node);
    }
    
    public Object getSingleData() {
        final Node node = this.composer.getSingleNode();
        if (node != null) {
            node.setType(this.rootType);
            return this.constructDocument(node);
        }
        return null;
    }
    
    private Object constructDocument(final Node node) {
        final Object data = this.constructObject(node);
        this.constructedObjects.clear();
        this.recursiveObjects.clear();
        return data;
    }
    
    protected Object constructObject(final Node node) {
        if (this.constructedObjects.containsKey(node)) {
            return this.constructedObjects.get(node);
        }
        if (this.recursiveObjects.containsKey(node)) {
            throw new ConstructorException(null, null, "found unconstructable recursive node", node.getStartMark());
        }
        this.recursiveObjects.put(node, null);
        final Object data = this.callConstructor(node);
        this.constructedObjects.put(node, data);
        this.recursiveObjects.remove(node);
        return data;
    }
    
    protected Object callConstructor(final Node node) {
        Object data = null;
        Construct constructor = null;
        constructor = this.yamlConstructors.get(node.getTag());
        if (constructor == null) {
            constructor = this.yamlConstructors.get(null);
            data = constructor.construct(node);
        }
        else {
            data = constructor.construct(node);
        }
        return data;
    }
    
    protected Object constructScalar(final ScalarNode node) {
        return node.getValue();
    }
    
    protected List<Object> createDefaultList(final int initSize) {
        return new LinkedList<Object>();
    }
    
    protected List<?> constructSequence(final SequenceNode node) {
        final List<Node> nodeValue = node.getValue();
        final List<Object> result = this.createDefaultList(nodeValue.size());
        for (final Node child : nodeValue) {
            result.add(this.constructObject(child));
        }
        return result;
    }
    
    protected Map<Object, Object> createDefaultMap() {
        return new LinkedHashMap<Object, Object>();
    }
    
    protected Map<Object, Object> constructMapping(final MappingNode node) {
        final Map<Object, Object> mapping = this.createDefaultMap();
        final List<Node[]> nodeValue = node.getValue();
        for (final Node[] tuple : nodeValue) {
            final Node keyNode = tuple[0];
            final Node valueNode = tuple[1];
            final Object key = this.constructObject(keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                }
                catch (Exception e) {
                    throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple[0].getStartMark());
                }
            }
            final Object value = this.constructObject(valueNode);
            mapping.put(key, value);
        }
        return mapping;
    }
}

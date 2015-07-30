// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.representer;

import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.MappingNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.SequenceNode;
import java.util.LinkedList;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import java.util.Iterator;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.io.IOException;
import com.newrelic.agent.deps.org.yaml.snakeyaml.serializer.Serializer;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import java.util.Map;

public abstract class BaseRepresenter
{
    protected final Map<Class, Represent> representers;
    protected Represent nullRepresenter;
    protected final Map<Class, Represent> multiRepresenters;
    private Character defaultStyle;
    protected Boolean defaultFlowStyle;
    protected final Map<Integer, Node> representedObjects;
    private final Set<Object> objectKeeper;
    protected Integer aliasKey;
    protected String rootTag;
    
    public BaseRepresenter(final Character default_style, final Boolean default_flow_style) {
        this.representers = new HashMap<Class, Represent>();
        this.multiRepresenters = new HashMap<Class, Represent>();
        this.representedObjects = new HashMap<Integer, Node>();
        this.objectKeeper = new HashSet<Object>();
        this.rootTag = null;
        this.defaultStyle = default_style;
        this.defaultFlowStyle = default_flow_style;
    }
    
    public void represent(final Serializer serializer, final Object data) throws IOException {
        final Node node = this.representData(data);
        serializer.serialize(node);
        this.representedObjects.clear();
        this.objectKeeper.clear();
    }
    
    protected Node representData(final Object data) {
        this.aliasKey = System.identityHashCode(data);
        if (!this.ignoreAliases(data) && this.representedObjects.containsKey(this.aliasKey)) {
            final Node node = this.representedObjects.get(this.aliasKey);
            return node;
        }
        if (data == null) {
            final Node node = this.nullRepresenter.representData(data);
            return node;
        }
        final Class clazz = data.getClass();
        Node node;
        if (this.representers.containsKey(clazz)) {
            final Represent representer = this.representers.get(clazz);
            node = representer.representData(data);
        }
        else {
            for (final Class repr : this.multiRepresenters.keySet()) {
                if (repr.isInstance(data)) {
                    final Represent representer2 = this.multiRepresenters.get(repr);
                    node = representer2.representData(data);
                    return node;
                }
            }
            if (clazz.isArray()) {
                throw new YAMLException("Arrays of primitives are not fully supported.");
            }
            if (this.multiRepresenters.containsKey(null)) {
                final Represent representer = this.multiRepresenters.get(null);
                node = representer.representData(data);
            }
            else {
                final Represent representer = this.representers.get(null);
                node = representer.representData(data);
            }
        }
        return node;
    }
    
    protected Node representScalar(final String tag, final String value, Character style) {
        if (style == null) {
            style = this.defaultStyle;
        }
        final Node node = new ScalarNode(tag, value, null, null, style);
        this.representedObjects.put(this.aliasKey, node);
        return node;
    }
    
    protected Node representScalar(final String tag, final String value) {
        return this.representScalar(tag, value, null);
    }
    
    protected Node representSequence(final String tag, final List<?> sequence, final Boolean flowStyle) {
        final List<Node> value = new LinkedList<Node>();
        final SequenceNode node = new SequenceNode(tag, value, flowStyle);
        this.representedObjects.put(this.aliasKey, node);
        boolean bestStyle = true;
        for (final Object item : sequence) {
            final Node nodeItem = this.representData(item);
            if (!(nodeItem instanceof ScalarNode) || ((ScalarNode)nodeItem).getStyle() != null) {
                bestStyle = false;
            }
            value.add(nodeItem);
        }
        if (flowStyle == null) {
            if (this.defaultFlowStyle != null) {
                node.setFlowStyle(this.defaultFlowStyle);
            }
            else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }
    
    protected Node representMapping(final String tag, final Map<?, Object> mapping, final Boolean flowStyle) {
        final List<Node[]> value = new LinkedList<Node[]>();
        final MappingNode node = new MappingNode(tag, value, flowStyle);
        this.representedObjects.put(this.aliasKey, node);
        boolean bestStyle = true;
        for (final Object itemKey : mapping.keySet()) {
            final Object itemValue = mapping.get(itemKey);
            final Node nodeKey = this.representData(itemKey);
            final Node nodeValue = this.representData(itemValue);
            if (!(nodeKey instanceof ScalarNode) || ((ScalarNode)nodeKey).getStyle() != null) {
                bestStyle = false;
            }
            if (!(nodeValue instanceof ScalarNode) || ((ScalarNode)nodeValue).getStyle() != null) {
                bestStyle = false;
            }
            value.add(new Node[] { nodeKey, nodeValue });
        }
        if (flowStyle == null) {
            if (this.defaultFlowStyle != null) {
                node.setFlowStyle(this.defaultFlowStyle);
            }
            else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }
    
    protected abstract boolean ignoreAliases(final Object p0);
}

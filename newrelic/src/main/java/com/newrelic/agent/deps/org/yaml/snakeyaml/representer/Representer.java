// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.representer;

import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.beans.PropertyDescriptor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.introspector.FieldProperty;
import java.lang.reflect.Modifier;
import com.newrelic.agent.deps.org.yaml.snakeyaml.introspector.MethodProperty;
import java.beans.Introspector;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.MappingNode;
import java.util.LinkedList;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import com.newrelic.agent.deps.org.yaml.snakeyaml.introspector.Property;
import java.util.Set;
import java.util.HashMap;
import com.newrelic.agent.deps.org.yaml.snakeyaml.TypeDescription;
import java.util.Map;

public class Representer extends SafeRepresenter
{
    private Map<Class<?>, String> classTags;
    private Map<Class<?>, TypeDescription> classDefinitions;
    
    public Representer(final Character default_style, final Boolean default_flow_style) {
        super(default_style, default_flow_style);
        this.classTags = new HashMap<Class<?>, String>();
        this.classDefinitions = new HashMap<Class<?>, TypeDescription>();
        this.representers.put(null, new RepresentJavaBean());
    }
    
    public Representer() {
        this(null, null);
    }
    
    public TypeDescription addTypeDescription(final TypeDescription definition) {
        if (definition == null) {
            throw new NullPointerException("ClassDescription is required.");
        }
        final String tag = definition.getTag();
        this.classTags.put(definition.getType(), tag);
        return this.classDefinitions.put(definition.getType(), definition);
    }
    
    private Node representMapping(final Set<Property> properties, final Object javaBean) {
        final List<Node[]> value = new LinkedList<Node[]>();
        final String customTag = this.classTags.get(javaBean.getClass());
        String tag;
        if (customTag == null) {
            if (this.rootTag == null) {
                tag = "tag:yaml.org,2002:" + javaBean.getClass().getName();
            }
            else {
                tag = "tag:yaml.org,2002:map";
            }
        }
        else {
            tag = customTag;
        }
        if (this.rootTag == null) {
            this.rootTag = tag;
        }
        final MappingNode node = new MappingNode(tag, value, null);
        this.representedObjects.put(this.aliasKey, node);
        boolean bestStyle = true;
        for (final Property property : properties) {
            final Node nodeKey = this.representData(property.getName());
            final Object memberValue = property.get(javaBean);
            final Node nodeValue = this.representData(memberValue);
            if (nodeValue instanceof MappingNode) {
                if (!Map.class.isAssignableFrom(memberValue.getClass()) && property.getType() != memberValue.getClass()) {
                    final String memberTag = "tag:yaml.org,2002:" + memberValue.getClass().getName();
                    nodeValue.setTag(memberTag);
                }
            }
            else if (memberValue != null && Enum.class.isAssignableFrom(memberValue.getClass())) {
                nodeValue.setTag("tag:yaml.org,2002:str");
            }
            if (!(nodeKey instanceof ScalarNode) || ((ScalarNode)nodeKey).getStyle() != null) {
                bestStyle = false;
            }
            if (!(nodeValue instanceof ScalarNode) || ((ScalarNode)nodeValue).getStyle() != null) {
                bestStyle = false;
            }
            value.add(new Node[] { nodeKey, nodeValue });
        }
        if (this.defaultFlowStyle != null) {
            node.setFlowStyle(this.defaultFlowStyle);
        }
        else {
            node.setFlowStyle(bestStyle);
        }
        return node;
    }
    
    private Set<Property> getProperties(final Class<?> type) throws IntrospectionException {
        final Set<Property> properties = new TreeSet<Property>();
        for (final PropertyDescriptor property : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
            if (property.getReadMethod() != null && !property.getReadMethod().getName().equals("getClass")) {
                properties.add(new MethodProperty(property));
            }
        }
        for (final Field field : type.getFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                if (!Modifier.isTransient(modifiers)) {
                    properties.add(new FieldProperty(field));
                }
            }
        }
        return properties;
    }
    
    private class RepresentJavaBean implements Represent
    {
        public Node representData(final Object data) {
            Set<Property> properties;
            try {
                properties = Representer.this.getProperties(data.getClass());
            }
            catch (IntrospectionException e) {
                throw new YAMLException(e);
            }
            final Node node = Representer.this.representMapping(properties, data);
            return node;
        }
    }
}

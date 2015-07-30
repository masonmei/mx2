// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.constructor;

import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.NodeId;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.Mark;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.beans.PropertyDescriptor;
import com.newrelic.agent.deps.org.yaml.snakeyaml.introspector.FieldProperty;
import java.lang.reflect.Modifier;
import com.newrelic.agent.deps.org.yaml.snakeyaml.introspector.MethodProperty;
import java.beans.Introspector;
import java.lang.reflect.Array;
import com.newrelic.agent.deps.org.yaml.snakeyaml.introspector.Property;
import java.util.Iterator;
import java.util.List;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.Date;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.MappingNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.SequenceNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.ScalarNode;
import com.newrelic.agent.deps.org.yaml.snakeyaml.nodes.Node;
import com.newrelic.agent.deps.org.yaml.snakeyaml.error.YAMLException;
import java.util.HashMap;
import com.newrelic.agent.deps.org.yaml.snakeyaml.TypeDescription;
import java.util.Map;

public class Constructor extends SafeConstructor
{
    private final Map<String, Class<?>> typeTags;
    private final Map<Class<?>, TypeDescription> typeDefinitions;
    
    public Constructor() {
        this(Object.class);
    }
    
    public Constructor(final Class<?> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        this.yamlConstructors.put(null, new ConstuctYamlObject());
        this.rootType = theRoot;
        this.typeTags = new HashMap<String, Class<?>>();
        this.typeDefinitions = new HashMap<Class<?>, TypeDescription>();
    }
    
    public Constructor(final String theRoot) throws ClassNotFoundException {
        this(Class.forName(check(theRoot)));
    }
    
    private static final String check(final String s) {
        if (s == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        if (s.trim().length() == 0) {
            throw new YAMLException("Root type must be provided.");
        }
        return s;
    }
    
    public TypeDescription addTypeDescription(final TypeDescription definition) {
        if (definition == null) {
            throw new NullPointerException("TypeDescription is required.");
        }
        if (this.rootType == Object.class && definition.isRoot()) {
            this.rootType = definition.getType();
        }
        final String tag = definition.getTag();
        this.typeTags.put(tag, definition.getType());
        return this.typeDefinitions.put(definition.getType(), definition);
    }
    
    protected Object callConstructor(final Node node) {
        if (Object.class.equals(node.getType())) {
            return super.callConstructor(node);
        }
        Object result = null;
        switch (node.getNodeId()) {
            case scalar: {
                result = this.constructScalarNode((ScalarNode)node);
                break;
            }
            case sequence: {
                result = this.constructSequence((SequenceNode)node);
                break;
            }
            default: {
                if (Map.class.isAssignableFrom(node.getType())) {
                    result = super.constructMapping((MappingNode)node);
                    break;
                }
                result = this.constructMappingNode((MappingNode)node);
                break;
            }
        }
        return result;
    }
    
    private Object constructScalarNode(final ScalarNode node) {
        final Class<?> type = node.getType();
        Object result;
        if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || type == Date.class || type == Character.class || type == BigInteger.class || Enum.class.isAssignableFrom(type)) {
            if (type == String.class) {
                final Construct stringContructor = this.yamlConstructors.get("tag:yaml.org,2002:str");
                result = stringContructor.construct(node);
            }
            else if (type == Boolean.class || type == Boolean.TYPE) {
                final Construct boolContructor = this.yamlConstructors.get("tag:yaml.org,2002:bool");
                result = boolContructor.construct(node);
            }
            else if (type == Character.class || type == Character.TYPE) {
                final Construct charContructor = this.yamlConstructors.get("tag:yaml.org,2002:str");
                final String ch = (String)charContructor.construct(node);
                if (ch.length() != 1) {
                    throw new YAMLException("Invalid node Character: '" + ch + "'; length: " + ch.length());
                }
                result = new Character(ch.charAt(0));
            }
            else if (type == Date.class) {
                final Construct dateContructor = this.yamlConstructors.get("tag:yaml.org,2002:timestamp");
                result = dateContructor.construct(node);
            }
            else if (type == Float.class || type == Double.class || type == Float.TYPE || type == Double.TYPE) {
                final Construct doubleContructor = this.yamlConstructors.get("tag:yaml.org,2002:float");
                result = doubleContructor.construct(node);
                if (type == Float.class || type == Float.TYPE) {
                    result = new Float((double)result);
                }
            }
            else if (Number.class.isAssignableFrom(type) || type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE) {
                final Construct intContructor = this.yamlConstructors.get("tag:yaml.org,2002:int");
                result = intContructor.construct(node);
                if (type == Byte.class || type == Byte.TYPE) {
                    result = new Byte(result.toString());
                }
                else if (type == Short.class || type == Short.TYPE) {
                    result = new Short(result.toString());
                }
                else if (type == Integer.class || type == Integer.TYPE) {
                    result = new Integer(result.toString());
                }
                else if (type == Long.class || type == Long.TYPE) {
                    result = new Long(result.toString());
                }
                else {
                    if (type != BigInteger.class) {
                        throw new YAMLException("Unsupported Number class: " + type);
                    }
                    result = new BigInteger(result.toString());
                }
            }
            else {
                if (!Enum.class.isAssignableFrom(type)) {
                    throw new YAMLException("Unsupported class: " + type);
                }
                final String tag = "tag:yaml.org,2002:" + type.getName();
                node.setTag(tag);
                result = super.callConstructor(node);
            }
        }
        else {
            try {
                final Object value = super.callConstructor(node);
                if (type.isArray()) {
                    result = value;
                }
                else {
                    final java.lang.reflect.Constructor<?> javaConstructor = type.getConstructor(value.getClass());
                    result = javaConstructor.newInstance(value);
                }
            }
            catch (Exception e) {
                throw new YAMLException(e);
            }
        }
        return result;
    }
    
    private Object constructMappingNode(final MappingNode node) {
        final Class<?> beanType = node.getType();
        Object object;
        try {
            object = beanType.newInstance();
        }
        catch (InstantiationException e) {
            throw new YAMLException(e);
        }
        catch (IllegalAccessException e2) {
            throw new YAMLException(e2);
        }
        final List<Node[]> nodeValue = node.getValue();
        for (final Node[] tuple : nodeValue) {
            if (!(tuple[0] instanceof ScalarNode)) {
                throw new YAMLException("Keys must be scalars but found: " + tuple[0]);
            }
            final ScalarNode keyNode = (ScalarNode)tuple[0];
            final Node valueNode = tuple[1];
            keyNode.setType(String.class);
            final String key = (String)this.constructObject(keyNode);
            boolean isArray = false;
            try {
                final Property property = this.getProperty(beanType, key);
                if (property == null) {
                    throw new YAMLException("Unable to find property '" + key + "' on class: " + beanType.getName());
                }
                valueNode.setType(property.getType());
                final TypeDescription memberDescription = this.typeDefinitions.get(beanType);
                if (memberDescription != null) {
                    switch (valueNode.getNodeId()) {
                        case sequence: {
                            final SequenceNode snode = (SequenceNode)valueNode;
                            final Class<?> memberType = memberDescription.getListPropertyType(key);
                            if (memberType != null) {
                                snode.setListType(memberType);
                                break;
                            }
                            if (property.getType().isArray()) {
                                isArray = true;
                                snode.setListType(property.getType().getComponentType());
                                break;
                            }
                            break;
                        }
                        case mapping: {
                            final MappingNode mnode = (MappingNode)valueNode;
                            final Class<?> keyType = memberDescription.getMapKeyType(key);
                            if (keyType != null) {
                                mnode.setKeyType(keyType);
                                mnode.setValueType(memberDescription.getMapValueType(key));
                                break;
                            }
                            break;
                        }
                    }
                }
                Object value = this.constructObject(valueNode);
                if (isArray) {
                    final List<Object> list = (List<Object>)value;
                    value = list.toArray((Object[])this.createArray(property.getType()));
                }
                property.set(object, value);
            }
            catch (Exception e3) {
                throw new YAMLException(e3);
            }
        }
        return object;
    }
    
    private <T> T[] createArray(final Class<T> type) {
        return (T[])Array.newInstance(type.getComponentType(), 0);
    }
    
    protected Property getProperty(final Class<?> type, final String name) throws IntrospectionException {
        final PropertyDescriptor[] arr$ = Introspector.getBeanInfo(type).getPropertyDescriptors();
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final PropertyDescriptor property = arr$[i$];
            if (property.getName().equals(name)) {
                if (property.getReadMethod() != null && property.getWriteMethod() != null) {
                    return new MethodProperty(property);
                }
                break;
            }
            else {
                ++i$;
            }
        }
        for (final Field field : type.getFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                if (!Modifier.isTransient(modifiers)) {
                    if (field.getName().equals(name)) {
                        return new FieldProperty(field);
                    }
                }
            }
        }
        return null;
    }
    
    private class ConstuctYamlObject implements Construct
    {
        public Object construct(final Node node) {
            Object result = null;
            final Class<?> customTag = Constructor.this.typeTags.get(node.getTag());
            try {
                Class cl;
                if (customTag == null) {
                    if (node.getTag().length() < "tag:yaml.org,2002:".length()) {
                        throw new YAMLException("Unknown tag: " + node.getTag());
                    }
                    final String name = node.getTag().substring("tag:yaml.org,2002:".length());
                    cl = Class.forName(name);
                }
                else {
                    cl = customTag;
                }
                switch (node.getNodeId()) {
                    case mapping: {
                        final MappingNode mnode = (MappingNode)node;
                        mnode.setType(cl);
                        result = Constructor.this.constructMappingNode(mnode);
                        break;
                    }
                    case sequence: {
                        final SequenceNode seqNode = (SequenceNode)node;
                        final List<Object> values = (List<Object>)Constructor.this.constructSequence(seqNode);
                        final Class[] parameterTypes = new Class[values.size()];
                        int index = 0;
                        for (final Object parameter : values) {
                            parameterTypes[index] = parameter.getClass();
                            ++index;
                        }
                        final java.lang.reflect.Constructor javaConstructor = cl.getConstructor((Class[])parameterTypes);
                        final Object[] initargs = values.toArray();
                        result = javaConstructor.newInstance(initargs);
                        break;
                    }
                    default: {
                        final ScalarNode scaNode = (ScalarNode)node;
                        final Object value = Constructor.this.constructScalar(scaNode);
                        if (Enum.class.isAssignableFrom(cl)) {
                            final String enumValueName = (String)node.getValue();
                            try {
                                result = Enum.valueOf((Class<Object>)cl, enumValueName);
                            }
                            catch (Exception ex) {
                                throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + cl.getName());
                            }
                            break;
                        }
                        final java.lang.reflect.Constructor javaConstructor = cl.getConstructor(value.getClass());
                        result = javaConstructor.newInstance(value);
                        break;
                    }
                }
            }
            catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark());
            }
            return result;
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.jaxb;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.lang.reflect.Field;
import org.w3c.dom.NodeList;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;

public class UnmarshallerFactory
{
    static final Map<Class<?>, Unmarshaller<?>> cachedUnmarshallers;
    
    public static <T> Unmarshaller<T> create(final Class<T> clazz) throws UnmarshalException {
        final Unmarshaller<?> cachedUnmarshaller = UnmarshallerFactory.cachedUnmarshallers.get(clazz);
        if (cachedUnmarshaller != null) {
            return (Unmarshaller<T>)cachedUnmarshaller;
        }
        final Unmarshaller<T> unmarshaller = create(clazz, (Map<Class<?>, Unmarshaller<?>>)Maps.newHashMap((Map<?, ?>)Unmarshaller.getDefaultUnmarshallers()));
        final Unmarshaller<T> newUnmarshaller = new Unmarshaller<T>(clazz) {
            public T unmarshall(final Node node) throws UnmarshalException {
                return unmarshaller.unmarshall(((Document)node).getDocumentElement());
            }
        };
        UnmarshallerFactory.cachedUnmarshallers.put(clazz, newUnmarshaller);
        return newUnmarshaller;
    }
    
    private static <T> Unmarshaller<T> create(final Class<T> clazz, final Map<Class<?>, Unmarshaller<?>> unmarshallers) throws UnmarshalException {
        try {
            final Setter attributesSetter = getAttributesSetter(clazz, unmarshallers);
            final Setter childrenSetter = getChildSetter(clazz, unmarshallers);
            final Unmarshaller<T> classUnmarshaller = new Unmarshaller<T>(clazz) {
                public T unmarshall(final Node node) throws UnmarshalException {
                    try {
                        final T newInstance = clazz.newInstance();
                        attributesSetter.set(newInstance, node);
                        childrenSetter.set(newInstance, node);
                        return newInstance;
                    }
                    catch (InstantiationException e) {
                        throw new UnmarshalException(e);
                    }
                    catch (IllegalAccessException e2) {
                        throw new UnmarshalException(e2);
                    }
                }
            };
            unmarshallers.put(clazz, classUnmarshaller);
            return classUnmarshaller;
        }
        catch (InstantiationException e) {
            throw new UnmarshalException(e);
        }
        catch (IllegalAccessException e2) {
            throw new UnmarshalException(e2);
        }
    }
    
    private static Setter getChildSetter(final Class<?> clazz, final Map<Class<?>, Unmarshaller<?>> unmarshallers) throws InstantiationException, IllegalAccessException, UnmarshalException {
        final Map<String, Setter> childSetters = (Map<String, Setter>)Maps.newHashMap();
        final XmlType type = clazz.getAnnotation(XmlType.class);
        if (type != null) {
            for (final String t : type.propOrder()) {
                if (!t.isEmpty()) {
                    try {
                        final Field field = clazz.getDeclaredField(t);
                        field.setAccessible(true);
                        final Class<?> fieldType = field.getType();
                        final Unmarshaller<?> unmarshaller = getUnmarshaller(unmarshallers, fieldType, field);
                        final XmlValue xmlValue = field.getAnnotation(XmlValue.class);
                        if (xmlValue != null) {
                            if (type.propOrder().length > 1) {
                                throw new UnmarshalException(clazz.getName() + " has an @XmlValue field so only one child type was expected, but multiple were found: " + type.propOrder());
                            }
                            return new ChildSetter(t, unmarshaller, field);
                        }
                        else {
                            childSetters.put(t, new ChildSetter(t, unmarshaller, field));
                        }
                    }
                    catch (Exception e) {
                        throw new UnmarshalException(e);
                    }
                }
            }
        }
        return new Setter() {
            public void set(final Object obj, final Node node) throws IllegalArgumentException, IllegalAccessException, InstantiationException, UnmarshalException {
                final NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    final Node item = childNodes.item(i);
                    if (item.getNodeType() != 8) {
                        String nodeName = item.getNodeName();
                        final String prefix = item.getPrefix();
                        if (prefix != null && !prefix.isEmpty()) {
                            nodeName = nodeName.substring(prefix.length() + 1);
                        }
                        final Setter setter = childSetters.get(nodeName);
                        if (setter == null) {
                            throw new UnmarshalException("No setter for node name " + nodeName + " on " + clazz.getName());
                        }
                        setter.set(obj, item);
                    }
                }
            }
        };
    }
    
    private static Setter getAttributesSetter(final Class<?> clazz, final Map<Class<?>, Unmarshaller<?>> unmarshallers) throws InstantiationException, IllegalAccessException, UnmarshalException {
        final List<Setter> attributeSetters = (List<Setter>)Lists.newArrayList();
        for (final Field field : clazz.getDeclaredFields()) {
            final XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
            if (attribute != null) {
                field.setAccessible(true);
                final Class<?> declaringClass = field.getType();
                final Unmarshaller<?> unmarshaller = getUnmarshaller(unmarshallers, declaringClass, field);
                attributeSetters.add(new AttributeSetter(attribute.name(), unmarshaller, field));
            }
        }
        return new Setter() {
            public void set(final Object obj, final Node node) throws IllegalArgumentException, IllegalAccessException, InstantiationException, UnmarshalException {
                if (node.getAttributes() != null) {
                    for (final Setter setter : attributeSetters) {
                        setter.set(obj, node);
                    }
                }
            }
        };
    }
    
    private static Unmarshaller<?> getUnmarshaller(final Map<Class<?>, Unmarshaller<?>> unmarshallers, Class<?> clazz, final Field field) throws InstantiationException, IllegalAccessException, UnmarshalException {
        if (clazz.isAssignableFrom(List.class)) {
            final ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
            clazz = (Class<?>)parameterizedType.getActualTypeArguments()[0];
        }
        Unmarshaller<?> unmarshaller = unmarshallers.get(clazz);
        if (unmarshaller == null) {
            unmarshaller = create(clazz, unmarshallers);
            unmarshallers.put(clazz, unmarshaller);
        }
        return unmarshaller;
    }
    
    static {
        cachedUnmarshallers = Maps.newConcurrentMap();
    }
    
    private static class ChildSetter implements Setter
    {
        private final Unmarshaller<?> unmarshaller;
        private final Field field;
        
        public ChildSetter(final String t, final Unmarshaller<?> unmarshaller, final Field field) {
            this.unmarshaller = unmarshaller;
            this.field = field;
        }
        
        public void set(final Object obj, final Node node) throws IllegalArgumentException, IllegalAccessException, InstantiationException, UnmarshalException {
            final Object value = this.unmarshaller.unmarshall(node);
            if (value != null) {
                if (this.field.getType().isAssignableFrom(List.class)) {
                    List<Object> list = (List<Object>)this.field.get(obj);
                    if (list == null) {
                        list = Lists.newArrayList();
                        this.field.set(obj, list);
                    }
                    list.add(value);
                }
                else {
                    this.field.set(obj, value);
                }
            }
        }
        
        public String toString() {
            return "ChildSetter [field=" + this.field + "]";
        }
    }
    
    private static class AttributeSetter implements Setter
    {
        private final String name;
        private final Unmarshaller<?> unmarshaller;
        private final Field field;
        
        public AttributeSetter(final String name, final Unmarshaller<?> unmarshaller, final Field field) {
            this.name = name;
            this.unmarshaller = unmarshaller;
            this.field = field;
        }
        
        public void set(final Object obj, final Node node) throws IllegalArgumentException, IllegalAccessException, InstantiationException, UnmarshalException {
            final Node namedItem = node.getAttributes().getNamedItem(this.name);
            if (namedItem != null) {
                final Object value = this.unmarshaller.unmarshall(namedItem);
                if (value != null) {
                    this.field.set(obj, value);
                }
            }
        }
        
        public String toString() {
            return "AttributeSetter [name=" + this.name + "]";
        }
    }
    
    private interface Setter
    {
        void set(Object p0, Node p1) throws IllegalArgumentException, IllegalAccessException, InstantiationException, UnmarshalException;
    }
}

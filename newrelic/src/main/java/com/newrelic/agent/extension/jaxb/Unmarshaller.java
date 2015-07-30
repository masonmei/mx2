// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.jaxb;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import org.w3c.dom.DOMException;
import java.text.MessageFormat;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import org.w3c.dom.Node;
import java.util.Map;

public abstract class Unmarshaller<T>
{
    private static final Map<Class<?>, Unmarshaller<?>> DEFAULT_UNMARSHALLERS;
    private final Class<?> type;
    
    public Unmarshaller(final Class<?> type) {
        this.type = type;
    }
    
    public abstract T unmarshall(final Node p0) throws UnmarshalException;
    
    public String toString() {
        return "Unmarshaller [type=" + this.type + "]";
    }
    
    private static Map<Class<?>, Unmarshaller<?>> createDefaultUnmarshallers() {
        final Map<Class<?>, Unmarshaller<?>> unmarshallers = (Map<Class<?>, Unmarshaller<?>>)Maps.newHashMap();
        unmarshallers.put(String.class, new Unmarshaller<String>(String.class) {
            public String unmarshall(final Node node) {
                if (node.getNodeType() != 1) {
                    return node.getNodeValue();
                }
                final Node firstChild = node.getFirstChild();
                if (firstChild == null) {
                    throw new DOMException((short)8, MessageFormat.format("The element node {0}  is present, but is empty.", node.getNodeName()));
                }
                return node.getFirstChild().getNodeValue();
            }
        });
        unmarshallers.put(Boolean.class, new Unmarshaller<Boolean>(Boolean.class) {
            public Boolean unmarshall(final Node node) {
                return Boolean.valueOf(node.getNodeValue());
            }
        });
        unmarshallers.put(Double.class, new Unmarshaller<Double>(Double.class) {
            public Double unmarshall(final Node node) {
                return Double.valueOf(node.getNodeValue());
            }
        });
        unmarshallers.put(Long.class, new Unmarshaller<Long>(Long.class) {
            public Long unmarshall(final Node node) {
                return Long.valueOf(node.getNodeValue());
            }
        });
        unmarshallers.put(Integer.class, new Unmarshaller<Integer>(Integer.class) {
            public Integer unmarshall(final Node node) {
                return Integer.valueOf(node.getNodeValue());
            }
        });
        return (Map<Class<?>, Unmarshaller<?>>)ImmutableMap.copyOf((Map<?, ?>)unmarshallers);
    }
    
    public static Map<? extends Class<?>, ? extends Unmarshaller<?>> getDefaultUnmarshallers() {
        return Unmarshaller.DEFAULT_UNMARSHALLERS;
    }
    
    static {
        DEFAULT_UNMARSHALLERS = createDefaultUnmarshallers();
    }
}

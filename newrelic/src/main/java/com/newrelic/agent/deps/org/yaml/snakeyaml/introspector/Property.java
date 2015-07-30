// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.introspector;

public abstract class Property implements Comparable<Property>
{
    private final String name;
    private final Class<?> type;
    
    public Property(final String name, final Class<?> type) {
        this.name = name;
        this.type = type;
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String toString() {
        return this.getName() + " of " + this.getType();
    }
    
    public int compareTo(final Property o) {
        return this.name.compareTo(o.name);
    }
    
    public abstract void set(final Object p0, final Object p1) throws Exception;
    
    public abstract Object get(final Object p0);
}

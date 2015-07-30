// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j;

import java.util.Iterator;
import java.io.Serializable;

public interface Marker extends Serializable
{
    public static final String ANY_MARKER = "*";
    public static final String ANY_NON_NULL_MARKER = "+";
    
    String getName();
    
    void add(Marker p0);
    
    boolean remove(Marker p0);
    
    boolean hasChildren();
    
    boolean hasReferences();
    
    Iterator iterator();
    
    boolean contains(Marker p0);
    
    boolean contains(String p0);
    
    boolean equals(Object p0);
    
    int hashCode();
}

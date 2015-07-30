// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.Iterator;
import java.util.Enumeration;

public class IteratorEnumeration<T> implements Enumeration<T>
{
    private final Iterator<T> it;
    
    public IteratorEnumeration(final Iterator<T> it) {
        this.it = it;
    }
    
    public boolean hasMoreElements() {
        return this.it.hasNext();
    }
    
    public T nextElement() {
        return this.it.next();
    }
}

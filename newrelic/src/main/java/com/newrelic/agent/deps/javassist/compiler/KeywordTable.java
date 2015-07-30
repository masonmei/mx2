// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.compiler;

import java.util.HashMap;

public final class KeywordTable extends HashMap
{
    public int lookup(final String name) {
        final Object found = this.get(name);
        if (found == null) {
            return -1;
        }
        return (Integer)found;
    }
    
    public void append(final String name, final int t) {
        this.put(name, new Integer(t));
    }
}

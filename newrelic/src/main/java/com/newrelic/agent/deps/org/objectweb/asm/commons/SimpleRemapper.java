// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Map;

public class SimpleRemapper extends Remapper
{
    private final Map mapping;
    
    public SimpleRemapper(final Map mapping) {
        this.mapping = mapping;
    }
    
    public SimpleRemapper(final String s, final String s2) {
        this.mapping = Collections.singletonMap(s, s2);
    }
    
    public String mapMethodName(final String s, final String s2, final String s3) {
        final String map = this.map(s + '.' + s2 + s3);
        return (map == null) ? s2 : map;
    }
    
    public String mapFieldName(final String s, final String s2, final String s3) {
        final String map = this.map(s + '.' + s2);
        return (map == null) ? s2 : map;
    }
    
    public String map(final String s) {
        return this.mapping.get(s);
    }
}

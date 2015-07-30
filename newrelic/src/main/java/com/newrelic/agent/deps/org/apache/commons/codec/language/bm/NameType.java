// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.language.bm;

public enum NameType
{
    ASHKENAZI("ash"), 
    GENERIC("gen"), 
    SEPHARDIC("sep");
    
    private final String name;
    
    private NameType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

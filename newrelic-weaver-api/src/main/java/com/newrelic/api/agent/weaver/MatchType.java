// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.api.agent.weaver;

public enum MatchType
{
    ExactClass(true), 
    BaseClass(false), 
    Interface(false);
    
    private final boolean exactMatch;
    
    private MatchType(final boolean exact) {
        this.exactMatch = exact;
    }
    
    public boolean isExactMatch() {
        return this.exactMatch;
    }
}

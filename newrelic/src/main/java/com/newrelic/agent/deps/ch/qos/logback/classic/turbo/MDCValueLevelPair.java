// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.turbo;

import com.newrelic.agent.deps.ch.qos.logback.classic.Level;

public class MDCValueLevelPair
{
    private String value;
    private Level level;
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String name) {
        this.value = name;
    }
    
    public Level getLevel() {
        return this.level;
    }
    
    public void setLevel(final Level level) {
        this.level = level;
    }
}

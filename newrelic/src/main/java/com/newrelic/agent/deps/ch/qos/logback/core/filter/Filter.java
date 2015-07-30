// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.filter;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class Filter<E> extends ContextAwareBase implements LifeCycle
{
    private String name;
    boolean start;
    
    public Filter() {
        this.start = false;
    }
    
    public void start() {
        this.start = true;
    }
    
    public boolean isStarted() {
        return this.start;
    }
    
    public void stop() {
        this.start = false;
    }
    
    public abstract FilterReply decide(final E p0);
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}

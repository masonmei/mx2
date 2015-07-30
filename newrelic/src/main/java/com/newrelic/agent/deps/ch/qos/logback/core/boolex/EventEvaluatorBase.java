// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.boolex;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class EventEvaluatorBase<E> extends ContextAwareBase implements EventEvaluator<E>
{
    String name;
    boolean started;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        if (this.name != null) {
            throw new IllegalStateException("name has been already set");
        }
        this.name = name;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void start() {
        this.started = true;
    }
    
    public void stop() {
        this.started = false;
    }
}

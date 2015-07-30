// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class TriggeringPolicyBase<E> extends ContextAwareBase implements TriggeringPolicy<E>
{
    private boolean start;
    
    public void start() {
        this.start = true;
    }
    
    public void stop() {
        this.start = false;
    }
    
    public boolean isStarted() {
        return this.start;
    }
}

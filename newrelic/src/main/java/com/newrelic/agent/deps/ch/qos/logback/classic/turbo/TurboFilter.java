// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.turbo;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class TurboFilter extends ContextAwareBase implements LifeCycle
{
    private String name;
    boolean start;
    
    public TurboFilter() {
        this.start = false;
    }
    
    public abstract FilterReply decide(final Marker p0, final Logger p1, final Level p2, final String p3, final Object[] p4, final Throwable p5);
    
    public void start() {
        this.start = true;
    }
    
    public boolean isStarted() {
        return this.start;
    }
    
    public void stop() {
        this.start = false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}

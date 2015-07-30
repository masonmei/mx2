// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterAttachable;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;

public interface Appender<E> extends LifeCycle, ContextAware, FilterAttachable<E>
{
    String getName();
    
    void doAppend(E p0) throws LogbackException;
    
    void setName(String p0);
}

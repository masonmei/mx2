// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.boolex;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;

public interface EventEvaluator<E> extends ContextAware, LifeCycle
{
    boolean evaluate(E p0) throws NullPointerException, EvaluationException;
    
    String getName();
    
    void setName(String p0);
}

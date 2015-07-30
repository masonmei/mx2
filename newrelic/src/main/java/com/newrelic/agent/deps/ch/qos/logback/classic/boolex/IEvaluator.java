// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.boolex;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public interface IEvaluator
{
    boolean doEvaluate(ILoggingEvent p0);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;

public interface LoggerContextAware extends ContextAware
{
    void setLoggerContext(LoggerContext p0);
}

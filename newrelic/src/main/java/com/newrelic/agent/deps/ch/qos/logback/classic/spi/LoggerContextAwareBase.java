// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class LoggerContextAwareBase extends ContextAwareBase implements LoggerContextAware
{
    public void setLoggerContext(final LoggerContext context) {
        super.setContext(context);
    }
    
    public void setContext(final Context context) {
        if (context instanceof LoggerContext || context == null) {
            super.setContext(context);
            return;
        }
        throw new IllegalArgumentException("LoggerContextAwareBase only accepts contexts of type c.l.classic.LoggerContext");
    }
    
    public LoggerContext getLoggerContext() {
        return (LoggerContext)this.context;
    }
}

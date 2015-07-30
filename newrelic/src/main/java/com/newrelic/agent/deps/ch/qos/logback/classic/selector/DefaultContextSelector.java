// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.selector;

import java.util.Arrays;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;

public class DefaultContextSelector implements ContextSelector
{
    private LoggerContext defaultLoggerContext;
    
    public DefaultContextSelector(final LoggerContext context) {
        this.defaultLoggerContext = context;
    }
    
    public LoggerContext getLoggerContext() {
        return this.getDefaultLoggerContext();
    }
    
    public LoggerContext getDefaultLoggerContext() {
        return this.defaultLoggerContext;
    }
    
    public LoggerContext detachLoggerContext(final String loggerContextName) {
        return this.defaultLoggerContext;
    }
    
    public List<String> getContextNames() {
        return Arrays.asList(this.defaultLoggerContext.getName());
    }
    
    public LoggerContext getLoggerContext(final String name) {
        if (this.defaultLoggerContext.getName().equals(name)) {
            return this.defaultLoggerContext;
        }
        return null;
    }
}

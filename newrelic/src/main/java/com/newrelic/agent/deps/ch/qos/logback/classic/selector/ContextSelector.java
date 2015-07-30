// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.selector;

import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;

public interface ContextSelector
{
    LoggerContext getLoggerContext();
    
    LoggerContext getLoggerContext(String p0);
    
    LoggerContext getDefaultLoggerContext();
    
    LoggerContext detachLoggerContext(String p0);
    
    List<String> getContextNames();
}

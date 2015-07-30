// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import java.util.Map;
import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.DeferredProcessingAware;

public interface ILoggingEvent extends DeferredProcessingAware
{
    String getThreadName();
    
    Level getLevel();
    
    String getMessage();
    
    Object[] getArgumentArray();
    
    String getFormattedMessage();
    
    String getLoggerName();
    
    LoggerContextVO getLoggerContextVO();
    
    IThrowableProxy getThrowableProxy();
    
    StackTraceElement[] getCallerData();
    
    boolean hasCallerData();
    
    Marker getMarker();
    
    Map<String, String> getMDCPropertyMap();
    
    Map<String, String> getMdc();
    
    long getTimeStamp();
    
    void prepareForDeferredProcessing();
}

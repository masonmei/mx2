// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.AsyncAppenderBase;

public class AsyncAppender extends AsyncAppenderBase<ILoggingEvent>
{
    boolean includeCallerData;
    
    public AsyncAppender() {
        this.includeCallerData = false;
    }
    
    protected boolean isDiscardable(final ILoggingEvent event) {
        final Level level = event.getLevel();
        return level.toInt() <= 20000;
    }
    
    protected void preprocess(final ILoggingEvent eventObject) {
        eventObject.prepareForDeferredProcessing();
        if (this.includeCallerData) {
            eventObject.getCallerData();
        }
    }
    
    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }
    
    public void setIncludeCallerData(final boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }
}

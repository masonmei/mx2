// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import java.io.Serializable;

public class LoggerRemoteView implements Serializable
{
    private static final long serialVersionUID = 5028223666108713696L;
    final LoggerContextVO loggerContextView;
    final String name;
    
    public LoggerRemoteView(final String name, final LoggerContext lc) {
        this.name = name;
        assert lc.getLoggerContextRemoteView() != null;
        this.loggerContextView = lc.getLoggerContextRemoteView();
    }
    
    public LoggerContextVO getLoggerContextView() {
        return this.loggerContextView;
    }
    
    public String getName() {
        return this.name;
    }
}

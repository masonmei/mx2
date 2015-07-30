// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.spi;

import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;

public interface LoggerContextListener
{
    boolean isResetResistant();
    
    void onStart(LoggerContext p0);
    
    void onReset(LoggerContext p0);
    
    void onStop(LoggerContext p0);
    
    void onLevelChange(Logger p0, Level p1);
}

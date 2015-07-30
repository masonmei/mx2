// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

public interface LifeCycle
{
    void start();
    
    void stop();
    
    boolean isStarted();
}

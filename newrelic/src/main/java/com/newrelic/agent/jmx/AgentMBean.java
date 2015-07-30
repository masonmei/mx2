// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

public interface AgentMBean
{
    boolean shutdown();
    
    boolean reconnect();
    
    boolean connect();
    
    String setLogLevel(String p0);
    
    String getLogLevel();
    
    boolean isStarted();
    
    boolean isConnected();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.config.AgentConfig;

public interface IAgentLogManager
{
    IAgentLogger getRootLogger();
    
    String getLogFilePath();
    
    void configureLogger(AgentConfig p0);
    
    void addConsoleHandler();
    
    void setLogLevel(String p0);
    
    String getLogLevel();
}

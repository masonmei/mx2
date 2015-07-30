// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.config.AgentConfig;

public class AgentLogManager
{
    static final String ROOT_LOGGER_NAME = "com.newrelic";
    private static final IAgentLogManager INSTANCE;
    private static final IAgentLogger ROOT_LOGGER;
    
    private static IAgentLogManager createAgentLogManager() {
        return LogbackLogManager.create("com.newrelic");
    }
    
    public static IAgentLogger getLogger() {
        return AgentLogManager.ROOT_LOGGER;
    }
    
    public static String getLogFilePath() {
        return AgentLogManager.INSTANCE.getLogFilePath();
    }
    
    public static void configureLogger(final AgentConfig agentConfig) {
        AgentLogManager.INSTANCE.configureLogger(agentConfig);
    }
    
    public static void addConsoleHandler() {
        AgentLogManager.INSTANCE.addConsoleHandler();
    }
    
    public static void setLogLevel(final String level) {
        AgentLogManager.INSTANCE.setLogLevel(level);
    }
    
    public static String getLogLevel() {
        return AgentLogManager.INSTANCE.getLogLevel();
    }
    
    static {
        INSTANCE = createAgentLogManager();
        ROOT_LOGGER = AgentLogManager.INSTANCE.getRootLogger();
    }
}

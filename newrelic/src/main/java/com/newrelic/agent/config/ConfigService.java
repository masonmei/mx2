// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Map;
import com.newrelic.agent.service.Service;

public interface ConfigService extends Service
{
    void addIAgentConfigListener(AgentConfigListener p0);
    
    void removeIAgentConfigListener(AgentConfigListener p0);
    
    Map<String, Object> getLocalSettings();
    
    Map<String, Object> getSanitizedLocalSettings();
    
    AgentConfig getDefaultAgentConfig();
    
    AgentConfig getLocalAgentConfig();
    
    AgentConfig getAgentConfig(String p0);
    
    TransactionTracerConfig getTransactionTracerConfig(String p0);
    
    ErrorCollectorConfig getErrorCollectorConfig(String p0);
    
    JarCollectorConfig getJarCollectorConfig(String p0);
    
    StripExceptionConfig getStripExceptionConfig(String p0);
}

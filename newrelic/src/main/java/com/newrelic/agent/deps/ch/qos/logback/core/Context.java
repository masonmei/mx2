// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import java.util.concurrent.ExecutorService;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PropertyContainer;

public interface Context extends PropertyContainer
{
    StatusManager getStatusManager();
    
    Object getObject(String p0);
    
    void putObject(String p0, Object p1);
    
    String getProperty(String p0);
    
    void putProperty(String p0, String p1);
    
    Map<String, String> getCopyOfPropertyMap();
    
    String getName();
    
    void setName(String p0);
    
    long getBirthTime();
    
    Object getConfigurationLock();
    
    ExecutorService getExecutorService();
}

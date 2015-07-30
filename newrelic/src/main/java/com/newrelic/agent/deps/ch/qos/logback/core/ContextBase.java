// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import com.newrelic.agent.deps.ch.qos.logback.core.util.EnvUtil;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LogbackLock;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;

public class ContextBase implements Context
{
    private long birthTime;
    private String name;
    private StatusManager sm;
    Map<String, String> propertyMap;
    Map<String, Object> objectMap;
    LogbackLock configurationLock;
    private static final int CORE_POOL_SIZE;
    ExecutorService executorService;
    
    public ContextBase() {
        this.birthTime = System.currentTimeMillis();
        this.sm = new BasicStatusManager();
        this.propertyMap = new HashMap<String, String>();
        this.objectMap = new HashMap<String, Object>();
        this.configurationLock = new LogbackLock();
        this.executorService = new ThreadPoolExecutor(ContextBase.CORE_POOL_SIZE, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }
    
    public StatusManager getStatusManager() {
        return this.sm;
    }
    
    public void setStatusManager(final StatusManager statusManager) {
        if (this.sm == null) {
            throw new IllegalArgumentException("null StatusManager not allowed");
        }
        this.sm = statusManager;
    }
    
    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap<String, String>(this.propertyMap);
    }
    
    public void putProperty(final String key, final String val) {
        this.propertyMap.put(key, val);
    }
    
    public String getProperty(final String key) {
        if ("CONTEXT_NAME".equals(key)) {
            return this.getName();
        }
        return this.propertyMap.get(key);
    }
    
    public Object getObject(final String key) {
        return this.objectMap.get(key);
    }
    
    public void putObject(final String key, final Object value) {
        this.objectMap.put(key, value);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void reset() {
        this.propertyMap.clear();
        this.objectMap.clear();
    }
    
    public void setName(final String name) throws IllegalStateException {
        if (name != null && name.equals(this.name)) {
            return;
        }
        if (this.name == null || "default".equals(this.name)) {
            this.name = name;
            return;
        }
        throw new IllegalStateException("Context has been already given a name");
    }
    
    public long getBirthTime() {
        return this.birthTime;
    }
    
    public Object getConfigurationLock() {
        return this.configurationLock;
    }
    
    public ExecutorService getExecutorService() {
        return this.executorService;
    }
    
    public String toString() {
        return this.name;
    }
    
    static {
        CORE_POOL_SIZE = (EnvUtil.isJDK5() ? 1 : 0);
    }
}

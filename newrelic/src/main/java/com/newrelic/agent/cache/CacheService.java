// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.cache;

import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.StatsEngine;
import java.util.Iterator;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import com.newrelic.agent.util.MethodCache;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.util.SingleClassLoader;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class CacheService extends AbstractService implements HarvestListener
{
    private static final long CLEAR_CACHE_INTERVAL;
    private final ConcurrentMap<String, SingleClassLoader> singleClassLoaders;
    private final ConcurrentMap<ClassMethodSignature, MethodCache> methodCaches;
    private final String defaultAppName;
    private volatile long lastTimeCacheCleared;
    
    public CacheService() {
        super(CacheService.class.getSimpleName());
        this.singleClassLoaders = new ConcurrentHashMap<String, SingleClassLoader>();
        this.methodCaches = new ConcurrentHashMap<ClassMethodSignature, MethodCache>();
        this.lastTimeCacheCleared = System.nanoTime();
        this.defaultAppName = ServiceFactory.getConfigService().getDefaultAgentConfig().getApplicationName();
    }
    
    protected void doStart() throws Exception {
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() throws Exception {
        ServiceFactory.getHarvestService().removeHarvestListener(this);
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public void afterHarvest(final String appName) {
        if (!appName.equals(this.defaultAppName)) {
            return;
        }
        final long timeNow = System.nanoTime();
        if (timeNow - this.lastTimeCacheCleared < CacheService.CLEAR_CACHE_INTERVAL) {
            return;
        }
        try {
            this.clearCaches();
            this.lastTimeCacheCleared = timeNow;
        }
        finally {
            this.lastTimeCacheCleared = timeNow;
        }
    }
    
    private void clearCaches() {
        for (final SingleClassLoader singleClassLoader : this.singleClassLoaders.values()) {
            singleClassLoader.clear();
        }
        for (final MethodCache methodCache : this.methodCaches.values()) {
            methodCache.clear();
        }
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
    }
    
    public SingleClassLoader getSingleClassLoader(final String className) {
        SingleClassLoader singleClassLoader = this.singleClassLoaders.get(className);
        if (singleClassLoader != null) {
            return singleClassLoader;
        }
        singleClassLoader = new SingleClassLoader(className);
        final SingleClassLoader oldSingleClassLoader = this.singleClassLoaders.putIfAbsent(className, singleClassLoader);
        return (oldSingleClassLoader == null) ? singleClassLoader : oldSingleClassLoader;
    }
    
    public MethodCache getMethodCache(final String className, final String methodName, final String methodDesc) {
        final ClassMethodSignature key = new ClassMethodSignature(className.replace('/', '.'), methodName, methodDesc);
        MethodCache methodCache = this.methodCaches.get(key);
        if (methodCache != null) {
            return methodCache;
        }
        methodCache = new MethodCache(methodName, (Class<?>[])new Class[0]);
        final MethodCache oldMethodCache = this.methodCaches.putIfAbsent(key, methodCache);
        return (oldMethodCache == null) ? methodCache : oldMethodCache;
    }
    
    static {
        CLEAR_CACHE_INTERVAL = TimeUnit.NANOSECONDS.convert(600L, TimeUnit.SECONDS);
    }
}

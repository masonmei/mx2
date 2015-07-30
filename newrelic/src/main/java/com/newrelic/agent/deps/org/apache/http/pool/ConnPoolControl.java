// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.pool;

public interface ConnPoolControl<T>
{
    void setMaxTotal(int p0);
    
    int getMaxTotal();
    
    void setDefaultMaxPerRoute(int p0);
    
    int getDefaultMaxPerRoute();
    
    void setMaxPerRoute(T p0, int p1);
    
    int getMaxPerRoute(T p0);
    
    PoolStats getTotalStats();
    
    PoolStats getStats(T p0);
}

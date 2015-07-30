// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import com.newrelic.agent.instrumentation.pointcuts.database.ConnectionFactory;
import java.sql.Connection;
import com.newrelic.agent.deps.com.google.common.cache.Cache;

public class ConnectionCache
{
    private static final int MAX_CONN_CACHE_SIZE = 50;
    private Cache<Connection, ConnectionFactory> connectionFactoryCache;
    
    public void putConnectionFactory(final Connection key, final ConnectionFactory val) {
        this.getOrCreateConnectionFactoryCache().put(key, val);
    }
    
    public long getConnectionFactoryCacheSize() {
        return this.getOrCreateConnectionFactoryCache().size();
    }
    
    public ConnectionFactory removeConnectionFactory(final Connection key) {
        if (this.connectionFactoryCache == null) {
            return null;
        }
        final ConnectionFactory cf = this.connectionFactoryCache.getIfPresent(key);
        this.connectionFactoryCache.invalidate(key);
        return cf;
    }
    
    public Cache<Connection, ConnectionFactory> getConnectionFactoryCache() {
        return this.connectionFactoryCache;
    }
    
    private Cache<Connection, ConnectionFactory> getOrCreateConnectionFactoryCache() {
        if (this.connectionFactoryCache == null) {
            this.connectionFactoryCache = CacheBuilder.newBuilder().maximumSize(50L).build();
        }
        return this.connectionFactoryCache;
    }
    
    public void clear() {
        this.connectionFactoryCache.invalidateAll();
    }
}

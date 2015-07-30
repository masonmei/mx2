// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.pool;

import com.newrelic.agent.deps.org.apache.http.pool.PoolEntry;
import com.newrelic.agent.deps.org.apache.http.config.ConnectionConfig;
import com.newrelic.agent.deps.org.apache.http.config.SocketConfig;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.pool.ConnFactory;
import java.util.concurrent.atomic.AtomicLong;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpClientConnection;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.pool.AbstractConnPool;

@ThreadSafe
public class BasicConnPool extends AbstractConnPool<HttpHost, HttpClientConnection, BasicPoolEntry>
{
    private static final AtomicLong COUNTER;
    
    public BasicConnPool(final ConnFactory<HttpHost, HttpClientConnection> connFactory) {
        super(connFactory, 2, 20);
    }
    
    public BasicConnPool(final HttpParams params) {
        super(new BasicConnFactory(params), 2, 20);
    }
    
    public BasicConnPool(final SocketConfig sconfig, final ConnectionConfig cconfig) {
        super(new BasicConnFactory(sconfig, cconfig), 2, 20);
    }
    
    public BasicConnPool() {
        super(new BasicConnFactory(SocketConfig.DEFAULT, ConnectionConfig.DEFAULT), 2, 20);
    }
    
    protected BasicPoolEntry createEntry(final HttpHost host, final HttpClientConnection conn) {
        return new BasicPoolEntry(Long.toString(BasicConnPool.COUNTER.getAndIncrement()), host, conn);
    }
    
    static {
        COUNTER = new AtomicLong();
    }
}

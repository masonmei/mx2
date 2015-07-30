// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import com.newrelic.agent.deps.org.apache.http.pool.PoolEntry;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.http.pool.ConnFactory;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import java.util.concurrent.atomic.AtomicLong;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.conn.ManagedHttpClientConnection;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.pool.AbstractConnPool;

@ThreadSafe
class CPool extends AbstractConnPool<HttpRoute, ManagedHttpClientConnection, CPoolEntry>
{
    private static final AtomicLong COUNTER;
    private final Log log;
    private final long timeToLive;
    private final TimeUnit tunit;
    
    public CPool(final ConnFactory<HttpRoute, ManagedHttpClientConnection> connFactory, final int defaultMaxPerRoute, final int maxTotal, final long timeToLive, final TimeUnit tunit) {
        super(connFactory, defaultMaxPerRoute, maxTotal);
        this.log = LogFactory.getLog(CPool.class);
        this.timeToLive = timeToLive;
        this.tunit = tunit;
    }
    
    protected CPoolEntry createEntry(final HttpRoute route, final ManagedHttpClientConnection conn) {
        final String id = Long.toString(CPool.COUNTER.getAndIncrement());
        return new CPoolEntry(this.log, id, route, conn, this.timeToLive, this.tunit);
    }
    
    static {
        COUNTER = new AtomicLong();
    }
}

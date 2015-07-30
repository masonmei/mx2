// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.pool;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpClientConnection;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.pool.PoolEntry;

@ThreadSafe
public class BasicPoolEntry extends PoolEntry<HttpHost, HttpClientConnection>
{
    public BasicPoolEntry(final String id, final HttpHost route, final HttpClientConnection conn) {
        super(id, route, conn);
    }
    
    public void close() {
        try {
            ((PoolEntry<T, HttpClientConnection>)this).getConnection().close();
        }
        catch (IOException ex) {}
    }
    
    public boolean isClosed() {
        return !((PoolEntry<T, HttpClientConnection>)this).getConnection().isOpen();
    }
}

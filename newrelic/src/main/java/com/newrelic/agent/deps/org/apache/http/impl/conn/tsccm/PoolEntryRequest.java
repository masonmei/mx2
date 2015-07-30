// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn.tsccm;

import com.newrelic.agent.deps.org.apache.http.conn.ConnectionPoolTimeoutException;
import java.util.concurrent.TimeUnit;

@Deprecated
public interface PoolEntryRequest
{
    BasicPoolEntry getPoolEntry(long p0, TimeUnit p1) throws InterruptedException, ConnectionPoolTimeoutException;
    
    void abortRequest();
}

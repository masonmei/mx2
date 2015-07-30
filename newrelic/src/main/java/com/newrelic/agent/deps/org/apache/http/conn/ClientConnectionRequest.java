// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.util.concurrent.TimeUnit;

@Deprecated
public interface ClientConnectionRequest
{
    ManagedClientConnection getConnection(long p0, TimeUnit p1) throws InterruptedException, ConnectionPoolTimeoutException;
    
    void abortRequest();
}

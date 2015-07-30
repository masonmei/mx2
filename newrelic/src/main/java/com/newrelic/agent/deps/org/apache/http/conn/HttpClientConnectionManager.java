// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.org.apache.http.HttpClientConnection;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;

public interface HttpClientConnectionManager
{
    ConnectionRequest requestConnection(HttpRoute p0, Object p1);
    
    void releaseConnection(HttpClientConnection p0, Object p1, long p2, TimeUnit p3);
    
    void connect(HttpClientConnection p0, HttpRoute p1, int p2, HttpContext p3) throws IOException;
    
    void upgrade(HttpClientConnection p0, HttpRoute p1, HttpContext p2) throws IOException;
    
    void routeComplete(HttpClientConnection p0, HttpRoute p1, HttpContext p2) throws IOException;
    
    void closeIdleConnections(long p0, TimeUnit p1);
    
    void closeExpiredConnections();
    
    void shutdown();
}

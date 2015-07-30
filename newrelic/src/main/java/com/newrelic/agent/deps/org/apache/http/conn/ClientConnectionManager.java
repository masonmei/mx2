// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.conn.scheme.SchemeRegistry;

@Deprecated
public interface ClientConnectionManager
{
    SchemeRegistry getSchemeRegistry();
    
    ClientConnectionRequest requestConnection(HttpRoute p0, Object p1);
    
    void releaseConnection(ManagedClientConnection p0, long p1, TimeUnit p2);
    
    void closeIdleConnections(long p0, TimeUnit p1);
    
    void closeExpiredConnections();
    
    void shutdown();
}

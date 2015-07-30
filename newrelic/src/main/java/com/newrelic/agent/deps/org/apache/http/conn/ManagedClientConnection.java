// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import javax.net.ssl.SSLSession;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.HttpClientConnection;

@Deprecated
public interface ManagedClientConnection extends HttpClientConnection, HttpRoutedConnection, ManagedHttpClientConnection, ConnectionReleaseTrigger
{
    boolean isSecure();
    
    HttpRoute getRoute();
    
    SSLSession getSSLSession();
    
    void open(HttpRoute p0, HttpContext p1, HttpParams p2) throws IOException;
    
    void tunnelTarget(boolean p0, HttpParams p1) throws IOException;
    
    void tunnelProxy(HttpHost p0, boolean p1, HttpParams p2) throws IOException;
    
    void layerProtocol(HttpContext p0, HttpParams p1) throws IOException;
    
    void markReusable();
    
    void unmarkReusable();
    
    boolean isMarkedReusable();
    
    void setState(Object p0);
    
    Object getState();
    
    void setIdleDuration(long p0, TimeUnit p1);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import javax.net.ssl.SSLSession;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.HttpInetConnection;

@Deprecated
public interface HttpRoutedConnection extends HttpInetConnection
{
    boolean isSecure();
    
    HttpRoute getRoute();
    
    SSLSession getSSLSession();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import java.io.IOException;
import java.net.Socket;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.HttpInetConnection;
import com.newrelic.agent.deps.org.apache.http.HttpClientConnection;

@Deprecated
public interface OperatedClientConnection extends HttpClientConnection, HttpInetConnection
{
    HttpHost getTargetHost();
    
    boolean isSecure();
    
    Socket getSocket();
    
    void opening(Socket p0, HttpHost p1) throws IOException;
    
    void openCompleted(boolean p0, HttpParams p1) throws IOException;
    
    void update(Socket p0, HttpHost p1, boolean p2, HttpParams p3) throws IOException;
}

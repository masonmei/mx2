// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.Socket;
import com.newrelic.agent.deps.org.apache.http.HttpInetConnection;
import com.newrelic.agent.deps.org.apache.http.HttpClientConnection;

public interface ManagedHttpClientConnection extends HttpClientConnection, HttpInetConnection
{
    String getId();
    
    void bind(Socket p0) throws IOException;
    
    Socket getSocket();
    
    SSLSession getSSLSession();
}

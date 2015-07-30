// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.scheme;

import com.newrelic.agent.deps.org.apache.http.conn.ConnectTimeoutException;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.Socket;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;

@Deprecated
public interface SchemeSocketFactory
{
    Socket createSocket(HttpParams p0) throws IOException;
    
    Socket connectSocket(Socket p0, InetSocketAddress p1, InetSocketAddress p2, HttpParams p3) throws IOException, UnknownHostException, ConnectTimeoutException;
    
    boolean isSecure(Socket p0) throws IllegalArgumentException;
}

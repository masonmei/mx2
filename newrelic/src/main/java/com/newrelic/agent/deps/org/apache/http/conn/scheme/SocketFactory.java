// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.scheme;

import com.newrelic.agent.deps.org.apache.http.conn.ConnectTimeoutException;
import java.net.UnknownHostException;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;

@Deprecated
public interface SocketFactory
{
    Socket createSocket() throws IOException;
    
    Socket connectSocket(Socket p0, String p1, int p2, InetAddress p3, int p4, HttpParams p5) throws IOException, UnknownHostException, ConnectTimeoutException;
    
    boolean isSecure(Socket p0) throws IllegalArgumentException;
}

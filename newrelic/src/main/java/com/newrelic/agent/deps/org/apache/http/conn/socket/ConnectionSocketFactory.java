// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.socket;

import java.net.InetSocketAddress;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import java.io.IOException;
import java.net.Socket;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;

public interface ConnectionSocketFactory
{
    Socket createSocket(HttpContext p0) throws IOException;
    
    Socket connectSocket(int p0, Socket p1, HttpHost p2, InetSocketAddress p3, InetSocketAddress p4, HttpContext p5) throws IOException;
}

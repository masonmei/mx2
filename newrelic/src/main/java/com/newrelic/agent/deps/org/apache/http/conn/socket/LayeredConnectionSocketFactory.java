// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.socket;

import java.net.UnknownHostException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import java.net.Socket;

public interface LayeredConnectionSocketFactory extends ConnectionSocketFactory
{
    Socket createLayeredSocket(Socket p0, String p1, int p2, HttpContext p3) throws IOException, UnknownHostException;
}

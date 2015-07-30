// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.scheme;

import java.net.UnknownHostException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import java.net.Socket;

@Deprecated
public interface SchemeLayeredSocketFactory extends SchemeSocketFactory
{
    Socket createLayeredSocket(Socket p0, String p1, int p2, HttpParams p3) throws IOException, UnknownHostException;
}

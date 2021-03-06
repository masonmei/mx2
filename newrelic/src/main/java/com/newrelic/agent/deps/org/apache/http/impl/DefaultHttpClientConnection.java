// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import java.net.Socket;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@Deprecated
@NotThreadSafe
public class DefaultHttpClientConnection extends SocketHttpClientConnection
{
    public void bind(final Socket socket, final HttpParams params) throws IOException {
        Args.notNull(socket, "Socket");
        Args.notNull(params, "HTTP parameters");
        this.assertNotOpen();
        socket.setTcpNoDelay(params.getBooleanParameter("http.tcp.nodelay", true));
        socket.setSoTimeout(params.getIntParameter("http.socket.timeout", 0));
        socket.setKeepAlive(params.getBooleanParameter("http.socket.keepalive", false));
        final int linger = params.getIntParameter("http.socket.linger", -1);
        if (linger >= 0) {
            socket.setSoLinger(linger > 0, linger);
        }
        super.bind(socket, params);
    }
}

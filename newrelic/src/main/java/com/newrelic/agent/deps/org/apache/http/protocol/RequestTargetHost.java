// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.net.InetAddress;
import com.newrelic.agent.deps.org.apache.http.HttpConnection;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.HttpInetConnection;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.HttpVersion;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpRequestInterceptor;

@Immutable
public class RequestTargetHost implements HttpRequestInterceptor
{
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        final HttpCoreContext corecontext = HttpCoreContext.adapt(context);
        final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("CONNECT") && ver.lessEquals(HttpVersion.HTTP_1_0)) {
            return;
        }
        if (!request.containsHeader("Host")) {
            HttpHost targethost = corecontext.getTargetHost();
            if (targethost == null) {
                final HttpConnection conn = corecontext.getConnection();
                if (conn instanceof HttpInetConnection) {
                    final InetAddress address = ((HttpInetConnection)conn).getRemoteAddress();
                    final int port = ((HttpInetConnection)conn).getRemotePort();
                    if (address != null) {
                        targethost = new HttpHost(address.getHostName(), port);
                    }
                }
                if (targethost == null) {
                    if (ver.lessEquals(HttpVersion.HTTP_1_0)) {
                        return;
                    }
                    throw new ProtocolException("Target host missing");
                }
            }
            request.addHeader("Host", targethost.toHostString());
        }
    }
}

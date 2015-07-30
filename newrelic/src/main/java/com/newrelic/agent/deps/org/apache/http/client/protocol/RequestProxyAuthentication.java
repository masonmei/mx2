// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.protocol;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.auth.AuthState;
import com.newrelic.agent.deps.org.apache.http.conn.HttpRoutedConnection;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Deprecated
@Immutable
public class RequestProxyAuthentication extends RequestAuthenticationBase
{
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        if (request.containsHeader("Proxy-Authorization")) {
            return;
        }
        final HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
        if (conn == null) {
            this.log.debug("HTTP connection not set in the context");
            return;
        }
        final HttpRoute route = conn.getRoute();
        if (route.isTunnelled()) {
            return;
        }
        final AuthState authState = (AuthState)context.getAttribute("http.auth.proxy-scope");
        if (authState == null) {
            this.log.debug("Proxy auth state not set in the context");
            return;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Proxy auth state: " + authState.getState());
        }
        this.process(authState, request, context);
    }
}

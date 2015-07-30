// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import java.net.URI;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpGet;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpHead;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpUriRequest;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.client.RedirectHandler;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.client.RedirectStrategy;

@Deprecated
@Immutable
class DefaultRedirectStrategyAdaptor implements RedirectStrategy
{
    private final RedirectHandler handler;
    
    public DefaultRedirectStrategyAdaptor(final RedirectHandler handler) {
        this.handler = handler;
    }
    
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        return this.handler.isRedirectRequested(response, context);
    }
    
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        final URI uri = this.handler.getLocationURI(response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        return new HttpGet(uri);
    }
    
    public RedirectHandler getHandler() {
        return this.handler;
    }
}

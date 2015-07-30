// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@Deprecated
@NotThreadSafe
public class RoutedRequest
{
    protected final RequestWrapper request;
    protected final HttpRoute route;
    
    public RoutedRequest(final RequestWrapper req, final HttpRoute route) {
        this.request = req;
        this.route = route;
    }
    
    public final RequestWrapper getRequest() {
        return this.request;
    }
    
    public final HttpRoute getRoute() {
        return this.route;
    }
}

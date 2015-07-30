// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.params;

import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import java.net.InetAddress;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.params.HttpAbstractParamBean;

@Deprecated
@NotThreadSafe
public class ConnRouteParamBean extends HttpAbstractParamBean
{
    public ConnRouteParamBean(final HttpParams params) {
        super(params);
    }
    
    public void setDefaultProxy(final HttpHost defaultProxy) {
        this.params.setParameter("http.route.default-proxy", defaultProxy);
    }
    
    public void setLocalAddress(final InetAddress address) {
        this.params.setParameter("http.route.local-address", address);
    }
    
    public void setForcedRoute(final HttpRoute route) {
        this.params.setParameter("http.route.forced-route", route);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.conn.SchemePortResolver;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class DefaultProxyRoutePlanner extends DefaultRoutePlanner
{
    private final HttpHost proxy;
    
    public DefaultProxyRoutePlanner(final HttpHost proxy, final SchemePortResolver schemePortResolver) {
        super(schemePortResolver);
        this.proxy = Args.notNull(proxy, "Proxy host");
    }
    
    public DefaultProxyRoutePlanner(final HttpHost proxy) {
        this(proxy, null);
    }
    
    protected HttpHost determineProxy(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
        return this.proxy;
    }
}

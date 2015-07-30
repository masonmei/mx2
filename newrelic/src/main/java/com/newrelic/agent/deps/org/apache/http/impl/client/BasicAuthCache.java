// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.conn.UnsupportedSchemeException;
import com.newrelic.agent.deps.org.apache.http.impl.conn.DefaultSchemePortResolver;
import com.newrelic.agent.deps.org.apache.http.conn.SchemePortResolver;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import java.util.HashMap;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.client.AuthCache;

@NotThreadSafe
public class BasicAuthCache implements AuthCache
{
    private final HashMap<HttpHost, AuthScheme> map;
    private final SchemePortResolver schemePortResolver;
    
    public BasicAuthCache(final SchemePortResolver schemePortResolver) {
        this.map = new HashMap<HttpHost, AuthScheme>();
        this.schemePortResolver = ((schemePortResolver != null) ? schemePortResolver : DefaultSchemePortResolver.INSTANCE);
    }
    
    public BasicAuthCache() {
        this(null);
    }
    
    protected HttpHost getKey(final HttpHost host) {
        if (host.getPort() <= 0) {
            int port;
            try {
                port = this.schemePortResolver.resolve(host);
            }
            catch (UnsupportedSchemeException ignore) {
                return host;
            }
            return new HttpHost(host.getHostName(), port, host.getSchemeName());
        }
        return host;
    }
    
    public void put(final HttpHost host, final AuthScheme authScheme) {
        Args.notNull(host, "HTTP host");
        this.map.put(this.getKey(host), authScheme);
    }
    
    public AuthScheme get(final HttpHost host) {
        Args.notNull(host, "HTTP host");
        return this.map.get(this.getKey(host));
    }
    
    public void remove(final HttpHost host) {
        Args.notNull(host, "HTTP host");
        this.map.remove(this.getKey(host));
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public String toString() {
        return this.map.toString();
    }
}

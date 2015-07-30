// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.impl.NoConnectionReuseStrategy;
import com.newrelic.agent.deps.org.apache.http.impl.DefaultConnectionReuseStrategy;
import com.newrelic.agent.deps.org.apache.http.ConnectionReuseStrategy;
import com.newrelic.agent.deps.org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import java.net.ProxySelector;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoutePlanner;
import com.newrelic.agent.deps.org.apache.http.impl.conn.PoolingClientConnectionManager;
import com.newrelic.agent.deps.org.apache.http.impl.conn.SchemeRegistryFactory;
import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionManager;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@Deprecated
@ThreadSafe
public class SystemDefaultHttpClient extends DefaultHttpClient
{
    public SystemDefaultHttpClient(final HttpParams params) {
        super(null, params);
    }
    
    public SystemDefaultHttpClient() {
        super(null, null);
    }
    
    protected ClientConnectionManager createClientConnectionManager() {
        final PoolingClientConnectionManager connmgr = new PoolingClientConnectionManager(SchemeRegistryFactory.createSystemDefault());
        String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
            s = System.getProperty("http.maxConnections", "5");
            final int max = Integer.parseInt(s);
            connmgr.setDefaultMaxPerRoute(max);
            connmgr.setMaxTotal(2 * max);
        }
        return connmgr;
    }
    
    protected HttpRoutePlanner createHttpRoutePlanner() {
        return new ProxySelectorRoutePlanner(this.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
    }
    
    protected ConnectionReuseStrategy createConnectionReuseStrategy() {
        final String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
            return new DefaultConnectionReuseStrategy();
        }
        return new NoConnectionReuseStrategy();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.conn.scheme.SchemeRegistry;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.org.apache.http.conn.ManagedClientConnection;
import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionRequest;
import com.newrelic.agent.deps.org.apache.http.conn.ClientConnectionManager;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.client.config.RequestConfig;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.client.ClientProtocolException;
import com.newrelic.agent.deps.org.apache.http.client.methods.Configurable;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.client.protocol.HttpClientContext;
import com.newrelic.agent.deps.org.apache.http.protocol.BasicHttpContext;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpRequestWrapper;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpExecutionAware;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.params.BasicHttpParams;
import com.newrelic.agent.deps.org.apache.http.conn.ConnectionKeepAliveStrategy;
import com.newrelic.agent.deps.org.apache.http.ConnectionReuseStrategy;
import com.newrelic.agent.deps.org.apache.http.impl.DefaultConnectionReuseStrategy;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpRequestExecutor;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.impl.execchain.MinimalClientExec;
import com.newrelic.agent.deps.org.apache.http.conn.HttpClientConnectionManager;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@ThreadSafe
class MinimalHttpClient extends CloseableHttpClient
{
    private final HttpClientConnectionManager connManager;
    private final MinimalClientExec requestExecutor;
    private final HttpParams params;
    
    public MinimalHttpClient(final HttpClientConnectionManager connManager) {
        this.connManager = Args.notNull(connManager, "HTTP connection manager");
        this.requestExecutor = new MinimalClientExec(new HttpRequestExecutor(), connManager, DefaultConnectionReuseStrategy.INSTANCE, DefaultConnectionKeepAliveStrategy.INSTANCE);
        this.params = new BasicHttpParams();
    }
    
    protected CloseableHttpResponse doExecute(final HttpHost target, final HttpRequest request, final HttpContext context) throws IOException, ClientProtocolException {
        Args.notNull(target, "Target host");
        Args.notNull(request, "HTTP request");
        HttpExecutionAware execAware = null;
        if (request instanceof HttpExecutionAware) {
            execAware = (HttpExecutionAware)request;
        }
        try {
            final HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request);
            final HttpClientContext localcontext = HttpClientContext.adapt((context != null) ? context : new BasicHttpContext());
            final HttpRoute route = new HttpRoute(target);
            RequestConfig config = null;
            if (request instanceof Configurable) {
                config = ((Configurable)request).getConfig();
            }
            if (config != null) {
                localcontext.setRequestConfig(config);
            }
            return this.requestExecutor.execute(route, wrapper, localcontext, execAware);
        }
        catch (HttpException httpException) {
            throw new ClientProtocolException(httpException);
        }
    }
    
    public HttpParams getParams() {
        return this.params;
    }
    
    public void close() {
        this.connManager.shutdown();
    }
    
    public ClientConnectionManager getConnectionManager() {
        return new ClientConnectionManager() {
            public void shutdown() {
                MinimalHttpClient.this.connManager.shutdown();
            }
            
            public ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
                throw new UnsupportedOperationException();
            }
            
            public void releaseConnection(final ManagedClientConnection conn, final long validDuration, final TimeUnit timeUnit) {
                throw new UnsupportedOperationException();
            }
            
            public SchemeRegistry getSchemeRegistry() {
                throw new UnsupportedOperationException();
            }
            
            public void closeIdleConnections(final long idletime, final TimeUnit tunit) {
                MinimalHttpClient.this.connManager.closeIdleConnections(idletime, tunit);
            }
            
            public void closeExpiredConnections() {
                MinimalHttpClient.this.connManager.closeExpiredConnections();
            }
        };
    }
}

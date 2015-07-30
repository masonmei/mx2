// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.execchain;

import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.auth.AuthState;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.http.client.config.RequestConfig;
import java.net.URI;
import java.util.List;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.util.EntityUtils;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.client.utils.URIUtils;
import com.newrelic.agent.deps.org.apache.http.HttpEntityEnclosingRequest;
import com.newrelic.agent.deps.org.apache.http.client.RedirectException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpExecutionAware;
import com.newrelic.agent.deps.org.apache.http.client.protocol.HttpClientContext;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpRequestWrapper;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoutePlanner;
import com.newrelic.agent.deps.org.apache.http.client.RedirectStrategy;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;

@ThreadSafe
public class RedirectExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final RedirectStrategy redirectStrategy;
    private final HttpRoutePlanner routePlanner;
    
    public RedirectExec(final ClientExecChain requestExecutor, final HttpRoutePlanner routePlanner, final RedirectStrategy redirectStrategy) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(requestExecutor, "HTTP client request executor");
        Args.notNull(routePlanner, "HTTP route planner");
        Args.notNull(redirectStrategy, "HTTP redirect strategy");
        this.requestExecutor = requestExecutor;
        this.routePlanner = routePlanner;
        this.redirectStrategy = redirectStrategy;
    }
    
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        final List<URI> redirectLocations = context.getRedirectLocations();
        if (redirectLocations != null) {
            redirectLocations.clear();
        }
        final RequestConfig config = context.getRequestConfig();
        final int maxRedirects = (config.getMaxRedirects() > 0) ? config.getMaxRedirects() : 50;
        HttpRoute currentRoute = route;
        HttpRequestWrapper currentRequest = request;
        int redirectCount = 0;
        while (true) {
            final CloseableHttpResponse response = this.requestExecutor.execute(currentRoute, currentRequest, context, execAware);
            try {
                if (!config.isRedirectsEnabled() || !this.redirectStrategy.isRedirected(currentRequest, response, context)) {
                    return response;
                }
                if (redirectCount >= maxRedirects) {
                    throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
                }
                ++redirectCount;
                final HttpRequest redirect = this.redirectStrategy.getRedirect(currentRequest, response, context);
                if (!redirect.headerIterator().hasNext()) {
                    final HttpRequest original = request.getOriginal();
                    redirect.setHeaders(original.getAllHeaders());
                }
                currentRequest = HttpRequestWrapper.wrap(redirect);
                if (currentRequest instanceof HttpEntityEnclosingRequest) {
                    Proxies.enhanceEntity((HttpEntityEnclosingRequest)currentRequest);
                }
                final URI uri = currentRequest.getURI();
                final HttpHost newTarget = URIUtils.extractHost(uri);
                if (newTarget == null) {
                    throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
                }
                if (!currentRoute.getTargetHost().equals(newTarget)) {
                    final AuthState targetAuthState = context.getTargetAuthState();
                    if (targetAuthState != null) {
                        this.log.debug("Resetting target auth state");
                        targetAuthState.reset();
                    }
                    final AuthState proxyAuthState = context.getProxyAuthState();
                    if (proxyAuthState != null) {
                        final AuthScheme authScheme = proxyAuthState.getAuthScheme();
                        if (authScheme != null && authScheme.isConnectionBased()) {
                            this.log.debug("Resetting proxy auth state");
                            proxyAuthState.reset();
                        }
                    }
                }
                currentRoute = this.routePlanner.determineRoute(newTarget, currentRequest, context);
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Redirecting to '" + uri + "' via " + currentRoute);
                }
                EntityUtils.consume(response.getEntity());
                response.close();
            }
            catch (RuntimeException ex) {
                response.close();
                throw ex;
            }
            catch (IOException ex2) {
                response.close();
                throw ex2;
            }
            catch (HttpException ex3) {
                try {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException ioex) {
                    this.log.debug("I/O error while releasing connection", ioex);
                }
                finally {
                    response.close();
                }
                throw ex3;
            }
        }
    }
}

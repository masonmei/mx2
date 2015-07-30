// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.execchain;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.Header;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.client.NonRepeatableRequestException;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpExecutionAware;
import com.newrelic.agent.deps.org.apache.http.client.protocol.HttpClientContext;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpRequestWrapper;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.http.client.HttpRequestRetryHandler;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class RetryExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final HttpRequestRetryHandler retryHandler;
    
    public RetryExec(final ClientExecChain requestExecutor, final HttpRequestRetryHandler retryHandler) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(requestExecutor, "HTTP request executor");
        Args.notNull(retryHandler, "HTTP request retry handler");
        this.requestExecutor = requestExecutor;
        this.retryHandler = retryHandler;
    }
    
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        final Header[] origheaders = request.getAllHeaders();
        int execCount = 1;
        try {
            return this.requestExecutor.execute(route, request, context, execAware);
        }
        catch (IOException ex) {
            if (execAware != null && execAware.isAborted()) {
                this.log.debug("Request has been aborted");
                throw ex;
            }
            if (!this.retryHandler.retryRequest(ex, execCount, context)) {
                throw ex;
            }
            if (this.log.isInfoEnabled()) {
                this.log.info("I/O exception (" + ex.getClass().getName() + ") caught when processing request: " + ex.getMessage());
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug(ex.getMessage(), ex);
            }
            if (!Proxies.isRepeatable(request)) {
                this.log.debug("Cannot retry non-repeatable request");
                throw new NonRepeatableRequestException("Cannot retry request with a non-repeatable request entity", ex);
            }
            request.setHeaders(origheaders);
            this.log.info("Retrying request");
            ++execCount;
            return this.requestExecutor.execute(route, request, context, execAware);
        }
    }
}

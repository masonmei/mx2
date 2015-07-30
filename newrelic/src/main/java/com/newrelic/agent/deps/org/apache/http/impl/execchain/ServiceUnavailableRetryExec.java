// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.execchain;

import com.newrelic.agent.deps.org.apache.http.HttpException;
import java.io.IOException;
import java.io.InterruptedIOException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.client.methods.CloseableHttpResponse;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpExecutionAware;
import com.newrelic.agent.deps.org.apache.http.client.protocol.HttpClientContext;
import com.newrelic.agent.deps.org.apache.http.client.methods.HttpRequestWrapper;
import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.http.client.ServiceUnavailableRetryStrategy;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class ServiceUnavailableRetryExec implements ClientExecChain
{
    private final Log log;
    private final ClientExecChain requestExecutor;
    private final ServiceUnavailableRetryStrategy retryStrategy;
    
    public ServiceUnavailableRetryExec(final ClientExecChain requestExecutor, final ServiceUnavailableRetryStrategy retryStrategy) {
        this.log = LogFactory.getLog(this.getClass());
        Args.notNull(requestExecutor, "HTTP request executor");
        Args.notNull(retryStrategy, "Retry strategy");
        this.requestExecutor = requestExecutor;
        this.retryStrategy = retryStrategy;
    }
    
    public CloseableHttpResponse execute(final HttpRoute route, final HttpRequestWrapper request, final HttpClientContext context, final HttpExecutionAware execAware) throws IOException, HttpException {
        int c = 1;
        while (true) {
            final CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);
            try {
                if (!this.retryStrategy.retryRequest(response, c, context)) {
                    return response;
                }
                response.close();
                final long nextInterval = this.retryStrategy.getRetryInterval();
                if (nextInterval > 0L) {
                    try {
                        this.log.trace("Wait for " + nextInterval);
                        Thread.sleep(nextInterval);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException();
                    }
                }
            }
            catch (RuntimeException ex) {
                response.close();
                throw ex;
            }
            ++c;
        }
    }
}

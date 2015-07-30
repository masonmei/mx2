// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.ProtocolVersion;
import com.newrelic.agent.deps.org.apache.http.HttpVersion;
import com.newrelic.agent.deps.org.apache.http.HttpEntityEnclosingRequest;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpRequestInterceptor;

@Immutable
public class RequestExpectContinue implements HttpRequestInterceptor
{
    private final boolean activeByDefault;
    
    public RequestExpectContinue() {
        this(false);
    }
    
    public RequestExpectContinue(final boolean activeByDefault) {
        this.activeByDefault = activeByDefault;
    }
    
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (!request.containsHeader("Expect") && request instanceof HttpEntityEnclosingRequest) {
            final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null && entity.getContentLength() != 0L && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                final boolean active = request.getParams().getBooleanParameter("http.protocol.expect-continue", this.activeByDefault);
                if (active) {
                    request.addHeader("Expect", "100-continue");
                }
            }
        }
    }
}

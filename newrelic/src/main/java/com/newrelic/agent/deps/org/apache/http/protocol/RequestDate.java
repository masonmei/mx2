// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.HttpEntityEnclosingRequest;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpRequestInterceptor;

@ThreadSafe
public class RequestDate implements HttpRequestInterceptor
{
    private static final HttpDateGenerator DATE_GENERATOR;
    
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (request instanceof HttpEntityEnclosingRequest && !request.containsHeader("Date")) {
            final String httpdate = RequestDate.DATE_GENERATOR.getCurrentDate();
            request.setHeader("Date", httpdate);
        }
    }
    
    static {
        DATE_GENERATOR = new HttpDateGenerator();
    }
}

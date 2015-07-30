// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.annotation.ThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpResponseInterceptor;

@ThreadSafe
public class ResponseDate implements HttpResponseInterceptor
{
    private static final HttpDateGenerator DATE_GENERATOR;
    
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        final int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && !response.containsHeader("Date")) {
            final String httpdate = ResponseDate.DATE_GENERATOR.getCurrentDate();
            response.setHeader("Date", httpdate);
        }
    }
    
    static {
        DATE_GENERATOR = new HttpDateGenerator();
    }
}

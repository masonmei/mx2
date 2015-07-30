// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;

public interface HttpResponseInterceptor
{
    void process(HttpResponse p0, HttpContext p1) throws HttpException, IOException;
}

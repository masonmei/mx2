// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;

public interface HttpResponseFactory
{
    HttpResponse newHttpResponse(ProtocolVersion p0, int p1, HttpContext p2);
    
    HttpResponse newHttpResponse(StatusLine p0, HttpContext p1);
}

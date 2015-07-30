// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import com.newrelic.agent.deps.org.apache.http.HttpRequest;

public interface HttpRequestHandlerMapper
{
    HttpRequestHandler lookup(HttpRequest p0);
}

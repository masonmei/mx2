// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;

public interface ServiceUnavailableRetryStrategy
{
    boolean retryRequest(HttpResponse p0, int p1, HttpContext p2);
    
    long getRetryInterval();
}

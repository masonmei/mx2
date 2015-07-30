// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import java.io.IOException;

public interface HttpRequestRetryHandler
{
    boolean retryRequest(IOException p0, int p1, HttpContext p2);
}

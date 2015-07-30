// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;

public interface ConnectionKeepAliveStrategy
{
    long getKeepAliveDuration(HttpResponse p0, HttpContext p1);
}

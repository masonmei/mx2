// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;

public interface ConnectionReuseStrategy
{
    boolean keepAlive(HttpResponse p0, HttpContext p1);
}

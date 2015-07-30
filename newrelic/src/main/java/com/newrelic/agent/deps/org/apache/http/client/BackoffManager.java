// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;

public interface BackoffManager
{
    void backOff(HttpRoute p0);
    
    void probe(HttpRoute p0);
}

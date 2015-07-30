// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn.params;

import com.newrelic.agent.deps.org.apache.http.conn.routing.HttpRoute;

@Deprecated
public interface ConnPerRoute
{
    int getMaxForRoute(HttpRoute p0);
}

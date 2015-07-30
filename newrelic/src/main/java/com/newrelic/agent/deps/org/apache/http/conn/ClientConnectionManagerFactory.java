// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import com.newrelic.agent.deps.org.apache.http.conn.scheme.SchemeRegistry;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;

@Deprecated
public interface ClientConnectionManagerFactory
{
    ClientConnectionManager newInstance(HttpParams p0, SchemeRegistry p1);
}

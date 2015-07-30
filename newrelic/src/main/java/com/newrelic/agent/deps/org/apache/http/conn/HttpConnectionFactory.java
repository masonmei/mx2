// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import com.newrelic.agent.deps.org.apache.http.config.ConnectionConfig;
import com.newrelic.agent.deps.org.apache.http.HttpConnection;

public interface HttpConnectionFactory<T, C extends HttpConnection>
{
    C create(T p0, ConnectionConfig p1);
}

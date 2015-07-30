// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import com.newrelic.agent.deps.org.apache.http.HttpHost;

public interface SchemePortResolver
{
    int resolve(HttpHost p0) throws UnsupportedSchemeException;
}

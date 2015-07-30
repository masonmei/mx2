// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.HttpHost;

public interface AuthCache
{
    void put(HttpHost p0, AuthScheme p1);
    
    AuthScheme get(HttpHost p0);
    
    void remove(HttpHost p0);
    
    void clear();
}

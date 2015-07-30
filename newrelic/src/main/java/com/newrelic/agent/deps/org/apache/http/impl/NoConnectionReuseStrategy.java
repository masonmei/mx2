// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.ConnectionReuseStrategy;

@Immutable
public class NoConnectionReuseStrategy implements ConnectionReuseStrategy
{
    public static final NoConnectionReuseStrategy INSTANCE;
    
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        return false;
    }
    
    static {
        INSTANCE = new NoConnectionReuseStrategy();
    }
}

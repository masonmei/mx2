// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.client.UserTokenHandler;

@Immutable
public class NoopUserTokenHandler implements UserTokenHandler
{
    public static final NoopUserTokenHandler INSTANCE;
    
    public Object getUserToken(final HttpContext context) {
        return null;
    }
    
    static {
        INSTANCE = new NoopUserTokenHandler();
    }
}

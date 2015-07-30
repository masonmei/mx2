// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.auth;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;

public interface AuthSchemeProvider
{
    AuthScheme create(HttpContext p0);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.auth;

import com.newrelic.agent.deps.org.apache.http.params.HttpParams;

@Deprecated
public interface AuthSchemeFactory
{
    AuthScheme newInstance(HttpParams p0);
}

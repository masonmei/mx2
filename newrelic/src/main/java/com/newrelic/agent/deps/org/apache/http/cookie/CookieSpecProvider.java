// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.cookie;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;

public interface CookieSpecProvider
{
    CookieSpec create(HttpContext p0);
}

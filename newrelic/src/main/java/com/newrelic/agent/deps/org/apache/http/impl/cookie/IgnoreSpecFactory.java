// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieSpec;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieSpecProvider;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieSpecFactory;

@Immutable
public class IgnoreSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    public CookieSpec newInstance(final HttpParams params) {
        return new IgnoreSpec();
    }
    
    public CookieSpec create(final HttpContext context) {
        return new IgnoreSpec();
    }
}

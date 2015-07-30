// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieOrigin;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieAttributeHandler;

@Immutable
public abstract class AbstractCookieAttributeHandler implements CookieAttributeHandler
{
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
    }
    
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return true;
    }
}

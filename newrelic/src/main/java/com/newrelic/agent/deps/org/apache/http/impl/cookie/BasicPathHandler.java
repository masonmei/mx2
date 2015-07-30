// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.cookie.CookieRestrictionViolationException;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieOrigin;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.util.TextUtils;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieAttributeHandler;

@Immutable
public class BasicPathHandler implements CookieAttributeHandler
{
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        cookie.setPath(TextUtils.isBlank(value) ? "/" : value);
    }
    
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        if (!this.match(cookie, origin)) {
            throw new CookieRestrictionViolationException("Illegal path attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
        }
    }
    
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        final String targetpath = origin.getPath();
        String topmostPath = cookie.getPath();
        if (topmostPath == null) {
            topmostPath = "/";
        }
        if (topmostPath.length() > 1 && topmostPath.endsWith("/")) {
            topmostPath = topmostPath.substring(0, topmostPath.length() - 1);
        }
        boolean match = targetpath.startsWith(topmostPath);
        if (match && targetpath.length() != topmostPath.length() && !topmostPath.endsWith("/")) {
            match = (targetpath.charAt(topmostPath.length()) == '/');
        }
        return match;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.cookie.CookieRestrictionViolationException;
import com.newrelic.agent.deps.org.apache.http.cookie.ClientCookie;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie2;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieOrigin;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieAttributeHandler;

@Immutable
public class RFC2965VersionAttributeHandler implements CookieAttributeHandler
{
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for version attribute");
        }
        int version = -1;
        try {
            version = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            version = -1;
        }
        if (version < 0) {
            throw new MalformedCookieException("Invalid cookie version.");
        }
        cookie.setVersion(version);
    }
    
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (cookie instanceof SetCookie2 && cookie instanceof ClientCookie && !((ClientCookie)cookie).containsAttribute("version")) {
            throw new CookieRestrictionViolationException("Violates RFC 2965. Version attribute is required.");
        }
    }
    
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return true;
    }
}

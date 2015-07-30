// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class BrowserCompatVersionAttributeHandler extends AbstractCookieAttributeHandler
{
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for version attribute");
        }
        int version = 0;
        try {
            version = Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {}
        cookie.setVersion(version);
    }
}

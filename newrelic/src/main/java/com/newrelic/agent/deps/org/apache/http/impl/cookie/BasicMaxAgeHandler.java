// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import java.util.Date;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class BasicMaxAgeHandler extends AbstractCookieAttributeHandler
{
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for max-age attribute");
        }
        int age;
        try {
            age = Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new MalformedCookieException("Invalid max-age attribute: " + value);
        }
        if (age < 0) {
            throw new MalformedCookieException("Negative max-age attribute: " + value);
        }
        cookie.setExpiryDate(new Date(System.currentTimeMillis() + age * 1000L));
    }
}

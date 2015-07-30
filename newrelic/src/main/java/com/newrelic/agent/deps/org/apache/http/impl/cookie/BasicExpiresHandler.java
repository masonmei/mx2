// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import java.util.Date;
import com.newrelic.agent.deps.org.apache.http.client.utils.DateUtils;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class BasicExpiresHandler extends AbstractCookieAttributeHandler
{
    private final String[] datepatterns;
    
    public BasicExpiresHandler(final String[] datepatterns) {
        Args.notNull(datepatterns, "Array of date patterns");
        this.datepatterns = datepatterns;
    }
    
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for expires attribute");
        }
        final Date expiry = DateUtils.parseDate(value, this.datepatterns);
        if (expiry == null) {
            throw new MalformedCookieException("Unable to parse expires attribute: " + value);
        }
        cookie.setExpiryDate(expiry);
    }
}

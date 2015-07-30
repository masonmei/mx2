// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.util.Args;
import java.util.Locale;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieRestrictionViolationException;
import java.util.StringTokenizer;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieOrigin;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class NetscapeDomainHandler extends BasicDomainHandler
{
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        super.validate(cookie, origin);
        final String host = origin.getHost();
        final String domain = cookie.getDomain();
        if (host.contains(".")) {
            final int domainParts = new StringTokenizer(domain, ".").countTokens();
            if (isSpecialDomain(domain)) {
                if (domainParts < 2) {
                    throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification for " + "special domains");
                }
            }
            else if (domainParts < 3) {
                throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification");
            }
        }
    }
    
    private static boolean isSpecialDomain(final String domain) {
        final String ucDomain = domain.toUpperCase(Locale.ENGLISH);
        return ucDomain.endsWith(".COM") || ucDomain.endsWith(".EDU") || ucDomain.endsWith(".NET") || ucDomain.endsWith(".GOV") || ucDomain.endsWith(".MIL") || ucDomain.endsWith(".ORG") || ucDomain.endsWith(".INT");
    }
    
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        final String host = origin.getHost();
        final String domain = cookie.getDomain();
        return domain != null && host.endsWith(domain);
    }
}

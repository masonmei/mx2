// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.protocol;

import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpException;
import com.newrelic.agent.deps.org.apache.http.HeaderIterator;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieOrigin;
import com.newrelic.agent.deps.org.apache.http.client.CookieStore;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieSpec;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpResponseInterceptor;

@Immutable
public class ResponseProcessCookies implements HttpResponseInterceptor
{
    private final Log log;
    
    public ResponseProcessCookies() {
        this.log = LogFactory.getLog(this.getClass());
    }
    
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP request");
        Args.notNull(context, "HTTP context");
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final CookieSpec cookieSpec = clientContext.getCookieSpec();
        if (cookieSpec == null) {
            this.log.debug("Cookie spec not specified in HTTP context");
            return;
        }
        final CookieStore cookieStore = clientContext.getCookieStore();
        if (cookieStore == null) {
            this.log.debug("Cookie store not specified in HTTP context");
            return;
        }
        final CookieOrigin cookieOrigin = clientContext.getCookieOrigin();
        if (cookieOrigin == null) {
            this.log.debug("Cookie origin not specified in HTTP context");
            return;
        }
        HeaderIterator it = response.headerIterator("Set-Cookie");
        this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        if (cookieSpec.getVersion() > 0) {
            it = response.headerIterator("Set-Cookie2");
            this.processCookies(it, cookieSpec, cookieOrigin, cookieStore);
        }
    }
    
    private void processCookies(final HeaderIterator iterator, final CookieSpec cookieSpec, final CookieOrigin cookieOrigin, final CookieStore cookieStore) {
        while (iterator.hasNext()) {
            final Header header = iterator.nextHeader();
            try {
                final List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
                for (final Cookie cookie : cookies) {
                    try {
                        cookieSpec.validate(cookie, cookieOrigin);
                        cookieStore.addCookie(cookie);
                        if (!this.log.isDebugEnabled()) {
                            continue;
                        }
                        this.log.debug("Cookie accepted: \"" + cookie + "\". ");
                    }
                    catch (MalformedCookieException ex) {
                        if (!this.log.isWarnEnabled()) {
                            continue;
                        }
                        this.log.warn("Cookie rejected: \"" + cookie + "\". " + ex.getMessage());
                    }
                }
            }
            catch (MalformedCookieException ex2) {
                if (!this.log.isWarnEnabled()) {
                    continue;
                }
                this.log.warn("Invalid cookie header: \"" + header + "\". " + ex2.getMessage());
            }
        }
    }
}

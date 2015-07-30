// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.cookie;

import com.newrelic.agent.deps.org.apache.http.cookie.CookieOrigin;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;
import com.newrelic.agent.deps.org.apache.http.cookie.MalformedCookieException;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie2;
import com.newrelic.agent.deps.org.apache.http.cookie.SetCookie;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieAttributeHandler;

@Immutable
public class RFC2965CommentUrlAttributeHandler implements CookieAttributeHandler
{
    public void parse(final SetCookie cookie, final String commenturl) throws MalformedCookieException {
        if (cookie instanceof SetCookie2) {
            final SetCookie2 cookie2 = (SetCookie2)cookie;
            cookie2.setCommentURL(commenturl);
        }
    }
    
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
    }
    
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return true;
    }
}

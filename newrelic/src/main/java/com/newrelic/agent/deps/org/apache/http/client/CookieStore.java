// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import java.util.Date;
import java.util.List;
import com.newrelic.agent.deps.org.apache.http.cookie.Cookie;

public interface CookieStore
{
    void addCookie(Cookie p0);
    
    List<Cookie> getCookies();
    
    boolean clearExpired(Date p0);
    
    void clear();
}

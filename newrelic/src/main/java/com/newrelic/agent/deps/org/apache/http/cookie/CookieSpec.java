// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.cookie;

import java.util.List;
import com.newrelic.agent.deps.org.apache.http.Header;

public interface CookieSpec
{
    int getVersion();
    
    List<Cookie> parse(Header p0, CookieOrigin p1) throws MalformedCookieException;
    
    void validate(Cookie p0, CookieOrigin p1) throws MalformedCookieException;
    
    boolean match(Cookie p0, CookieOrigin p1);
    
    List<Header> formatCookies(List<Cookie> p0);
    
    Header getVersionHeader();
}

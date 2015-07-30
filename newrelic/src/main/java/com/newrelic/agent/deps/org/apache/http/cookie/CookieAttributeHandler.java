// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.cookie;

public interface CookieAttributeHandler
{
    void parse(SetCookie p0, String p1) throws MalformedCookieException;
    
    void validate(Cookie p0, CookieOrigin p1) throws MalformedCookieException;
    
    boolean match(Cookie p0, CookieOrigin p1);
}

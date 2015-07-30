// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.cookie;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class CookieRestrictionViolationException extends MalformedCookieException
{
    private static final long serialVersionUID = 7371235577078589013L;
    
    public CookieRestrictionViolationException() {
    }
    
    public CookieRestrictionViolationException(final String message) {
        super(message);
    }
}

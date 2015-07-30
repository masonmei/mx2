// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class LaxRedirectStrategy extends DefaultRedirectStrategy
{
    private static final String[] REDIRECT_METHODS;
    
    protected boolean isRedirectable(final String method) {
        for (final String m : LaxRedirectStrategy.REDIRECT_METHODS) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        REDIRECT_METHODS = new String[] { "GET", "POST", "HEAD" };
    }
}

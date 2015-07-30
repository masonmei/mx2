// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.auth;

import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public final class AuthOption
{
    private final AuthScheme authScheme;
    private final Credentials creds;
    
    public AuthOption(final AuthScheme authScheme, final Credentials creds) {
        Args.notNull(authScheme, "Auth scheme");
        Args.notNull(creds, "User credentials");
        this.authScheme = authScheme;
        this.creds = creds;
    }
    
    public AuthScheme getAuthScheme() {
        return this.authScheme;
    }
    
    public Credentials getCredentials() {
        return this.creds;
    }
    
    public String toString() {
        return this.authScheme.toString();
    }
}

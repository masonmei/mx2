// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.auth;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.auth.AuthSchemeProvider;
import com.newrelic.agent.deps.org.apache.http.auth.AuthSchemeFactory;

@Immutable
public class KerberosSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    private final boolean stripPort;
    
    public KerberosSchemeFactory(final boolean stripPort) {
        this.stripPort = stripPort;
    }
    
    public KerberosSchemeFactory() {
        this(false);
    }
    
    public boolean isStripPort() {
        return this.stripPort;
    }
    
    public AuthScheme newInstance(final HttpParams params) {
        return new KerberosScheme(this.stripPort);
    }
    
    public AuthScheme create(final HttpContext context) {
        return new KerberosScheme(this.stripPort);
    }
}

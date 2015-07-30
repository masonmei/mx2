// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.auth;

import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import com.newrelic.agent.deps.org.apache.http.auth.AuthSchemeFactory;

@Deprecated
public class NegotiateSchemeFactory implements AuthSchemeFactory
{
    private final SpnegoTokenGenerator spengoGenerator;
    private final boolean stripPort;
    
    public NegotiateSchemeFactory(final SpnegoTokenGenerator spengoGenerator, final boolean stripPort) {
        this.spengoGenerator = spengoGenerator;
        this.stripPort = stripPort;
    }
    
    public NegotiateSchemeFactory(final SpnegoTokenGenerator spengoGenerator) {
        this(spengoGenerator, false);
    }
    
    public NegotiateSchemeFactory() {
        this(null, false);
    }
    
    public AuthScheme newInstance(final HttpParams params) {
        return new NegotiateScheme(this.spengoGenerator, this.stripPort);
    }
    
    public boolean isStripPort() {
        return this.stripPort;
    }
    
    public SpnegoTokenGenerator getSpengoGenerator() {
        return this.spengoGenerator;
    }
}

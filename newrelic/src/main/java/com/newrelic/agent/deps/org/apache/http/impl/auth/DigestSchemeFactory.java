// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.auth;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import java.nio.charset.Charset;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.auth.AuthSchemeProvider;
import com.newrelic.agent.deps.org.apache.http.auth.AuthSchemeFactory;

@Immutable
public class DigestSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    private final Charset charset;
    
    public DigestSchemeFactory(final Charset charset) {
        this.charset = charset;
    }
    
    public DigestSchemeFactory() {
        this(null);
    }
    
    public AuthScheme newInstance(final HttpParams params) {
        return new DigestScheme();
    }
    
    public AuthScheme create(final HttpContext context) {
        return new DigestScheme(this.charset);
    }
}

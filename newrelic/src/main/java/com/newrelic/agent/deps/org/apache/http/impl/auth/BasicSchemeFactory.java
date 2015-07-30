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
public class BasicSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    private final Charset charset;
    
    public BasicSchemeFactory(final Charset charset) {
        this.charset = charset;
    }
    
    public BasicSchemeFactory() {
        this(null);
    }
    
    public AuthScheme newInstance(final HttpParams params) {
        return new BasicScheme();
    }
    
    public AuthScheme create(final HttpContext context) {
        return new BasicScheme(this.charset);
    }
}

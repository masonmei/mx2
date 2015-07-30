// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.protocol;

import com.newrelic.agent.deps.org.apache.http.client.CredentialsProvider;
import com.newrelic.agent.deps.org.apache.http.client.CookieStore;
import com.newrelic.agent.deps.org.apache.http.auth.AuthSchemeRegistry;
import com.newrelic.agent.deps.org.apache.http.cookie.CookieSpecRegistry;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@Deprecated
@NotThreadSafe
public class ClientContextConfigurer implements ClientContext
{
    private final HttpContext context;
    
    public ClientContextConfigurer(final HttpContext context) {
        Args.notNull(context, "HTTP context");
        this.context = context;
    }
    
    public void setCookieSpecRegistry(final CookieSpecRegistry registry) {
        this.context.setAttribute("http.cookiespec-registry", registry);
    }
    
    public void setAuthSchemeRegistry(final AuthSchemeRegistry registry) {
        this.context.setAttribute("http.authscheme-registry", registry);
    }
    
    public void setCookieStore(final CookieStore store) {
        this.context.setAttribute("http.cookie-store", store);
    }
    
    public void setCredentialsProvider(final CredentialsProvider provider) {
        this.context.setAttribute("http.auth.credentials-provider", provider);
    }
}

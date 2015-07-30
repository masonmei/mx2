// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.auth.AuthState;
import com.newrelic.agent.deps.org.apache.http.client.AuthenticationStrategy;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;

@Deprecated
public class HttpAuthenticator extends com.newrelic.agent.deps.org.apache.http.impl.auth.HttpAuthenticator
{
    public HttpAuthenticator(final Log log) {
        super(log);
    }
    
    public HttpAuthenticator() {
    }
    
    public boolean authenticate(final HttpHost host, final HttpResponse response, final AuthenticationStrategy authStrategy, final AuthState authState, final HttpContext context) {
        return this.handleAuthChallenge(host, response, authStrategy, authState, context);
    }
}

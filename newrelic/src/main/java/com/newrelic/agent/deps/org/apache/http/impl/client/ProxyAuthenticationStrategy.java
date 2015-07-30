// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.auth.MalformedChallengeException;
import com.newrelic.agent.deps.org.apache.http.Header;
import java.util.Queue;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import java.util.Map;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.HttpHost;
import java.util.Collection;
import com.newrelic.agent.deps.org.apache.http.client.config.RequestConfig;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public static final ProxyAuthenticationStrategy INSTANCE;
    
    public ProxyAuthenticationStrategy() {
        super(407, "Proxy-Authenticate");
    }
    
    Collection<String> getPreferredAuthSchemes(final RequestConfig config) {
        return config.getProxyPreferredAuthSchemes();
    }
    
    static {
        INSTANCE = new ProxyAuthenticationStrategy();
    }
}

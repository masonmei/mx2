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
public class TargetAuthenticationStrategy extends AuthenticationStrategyImpl
{
    public static final TargetAuthenticationStrategy INSTANCE;
    
    public TargetAuthenticationStrategy() {
        super(401, "WWW-Authenticate");
    }
    
    Collection<String> getPreferredAuthSchemes(final RequestConfig config) {
        return config.getTargetPreferredAuthSchemes();
    }
    
    static {
        INSTANCE = new TargetAuthenticationStrategy();
    }
}

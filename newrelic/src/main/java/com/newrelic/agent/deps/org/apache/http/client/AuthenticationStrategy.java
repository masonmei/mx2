// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.auth.AuthOption;
import java.util.Queue;
import com.newrelic.agent.deps.org.apache.http.auth.MalformedChallengeException;
import com.newrelic.agent.deps.org.apache.http.Header;
import java.util.Map;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpHost;

public interface AuthenticationStrategy
{
    boolean isAuthenticationRequested(HttpHost p0, HttpResponse p1, HttpContext p2);
    
    Map<String, Header> getChallenges(HttpHost p0, HttpResponse p1, HttpContext p2) throws MalformedChallengeException;
    
    Queue<AuthOption> select(Map<String, Header> p0, HttpHost p1, HttpResponse p2, HttpContext p3) throws MalformedChallengeException;
    
    void authSucceeded(HttpHost p0, AuthScheme p1, HttpContext p2);
    
    void authFailed(HttpHost p0, AuthScheme p1, HttpContext p2);
}

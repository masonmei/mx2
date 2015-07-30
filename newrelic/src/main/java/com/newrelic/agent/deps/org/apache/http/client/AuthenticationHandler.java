// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.auth.AuthenticationException;
import com.newrelic.agent.deps.org.apache.http.auth.AuthScheme;
import com.newrelic.agent.deps.org.apache.http.auth.MalformedChallengeException;
import com.newrelic.agent.deps.org.apache.http.Header;
import java.util.Map;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;

@Deprecated
public interface AuthenticationHandler
{
    boolean isAuthenticationRequested(HttpResponse p0, HttpContext p1);
    
    Map<String, Header> getChallenges(HttpResponse p0, HttpContext p1) throws MalformedChallengeException;
    
    AuthScheme selectScheme(Map<String, Header> p0, HttpResponse p1, HttpContext p2) throws AuthenticationException;
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.auth;

import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.Header;

public interface AuthScheme
{
    void processChallenge(Header p0) throws MalformedChallengeException;
    
    String getSchemeName();
    
    String getParameter(String p0);
    
    String getRealm();
    
    boolean isConnectionBased();
    
    boolean isComplete();
    
    @Deprecated
    Header authenticate(Credentials p0, HttpRequest p1) throws AuthenticationException;
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.auth;

import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;

public interface ContextAwareAuthScheme extends AuthScheme
{
    Header authenticate(Credentials p0, HttpRequest p1, HttpContext p2) throws AuthenticationException;
}

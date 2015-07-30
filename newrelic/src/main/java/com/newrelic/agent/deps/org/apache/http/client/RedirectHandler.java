// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import java.net.URI;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;

@Deprecated
public interface RedirectHandler
{
    boolean isRedirectRequested(HttpResponse p0, HttpContext p1);
    
    URI getLocationURI(HttpResponse p0, HttpContext p1) throws ProtocolException;
}

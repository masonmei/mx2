// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.client.methods.HttpUriRequest;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.protocol.HttpContext;
import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;

public interface RedirectStrategy
{
    boolean isRedirected(HttpRequest p0, HttpResponse p1, HttpContext p2) throws ProtocolException;
    
    HttpUriRequest getRedirect(HttpRequest p0, HttpResponse p1, HttpContext p2) throws ProtocolException;
}

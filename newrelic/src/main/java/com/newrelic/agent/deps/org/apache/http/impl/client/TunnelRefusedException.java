// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import com.newrelic.agent.deps.org.apache.http.HttpResponse;
import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.HttpException;

@Deprecated
@Immutable
public class TunnelRefusedException extends HttpException
{
    private static final long serialVersionUID = -8646722842745617323L;
    private final HttpResponse response;
    
    public TunnelRefusedException(final String message, final HttpResponse response) {
        super(message);
        this.response = response;
    }
    
    public HttpResponse getResponse() {
        return this.response;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.params;

import com.newrelic.agent.deps.org.apache.http.util.Args;

@Deprecated
public abstract class HttpAbstractParamBean
{
    protected final HttpParams params;
    
    public HttpAbstractParamBean(final HttpParams params) {
        this.params = Args.notNull(params, "HTTP parameters");
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.netty;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Response;

public class DelegatingNettyHttpResponse implements Response
{
    private volatile NettyHttpResponse delegate;
    
    private DelegatingNettyHttpResponse(final NettyHttpResponse delegate) {
        this.delegate = delegate;
    }
    
    public void setDelegate(final NettyHttpResponse delegate) {
        this.delegate = delegate;
    }
    
    static Response create(final NettyHttpResponse delegate) {
        return (Response)new DelegatingNettyHttpResponse(delegate);
    }
    
    public HeaderType getHeaderType() {
        return HeaderType.HTTP;
    }
    
    public int getStatus() throws Exception {
        return (this.delegate == null) ? 0 : this.delegate._nr_status().getCode();
    }
    
    public String getStatusMessage() throws Exception {
        return (this.delegate == null) ? "" : this.delegate._nr_status().getReasonPhrase();
    }
    
    public String getContentType() {
        return null;
    }
    
    public void setHeader(final String name, final String value) {
        if (this.delegate == null) {
            return;
        }
        this.delegate.setHeader(name, value);
    }
}

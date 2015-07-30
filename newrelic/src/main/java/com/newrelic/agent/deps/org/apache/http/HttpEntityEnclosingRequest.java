// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public interface HttpEntityEnclosingRequest extends HttpRequest
{
    boolean expectContinue();
    
    void setEntity(HttpEntity p0);
    
    HttpEntity getEntity();
}

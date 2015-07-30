// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.methods;

import com.newrelic.agent.deps.org.apache.http.client.utils.CloneUtils;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpEntityEnclosingRequest;

@NotThreadSafe
public abstract class HttpEntityEnclosingRequestBase extends HttpRequestBase implements HttpEntityEnclosingRequest
{
    private HttpEntity entity;
    
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }
    
    public boolean expectContinue() {
        final Header expect = this.getFirstHeader("Expect");
        return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HttpEntityEnclosingRequestBase clone = (HttpEntityEnclosingRequestBase)super.clone();
        if (this.entity != null) {
            clone.entity = CloneUtils.cloneObject(this.entity);
        }
        return clone;
    }
}

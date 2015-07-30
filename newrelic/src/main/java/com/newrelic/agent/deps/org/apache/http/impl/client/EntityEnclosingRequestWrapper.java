// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.client;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.entity.HttpEntityWrapper;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;
import com.newrelic.agent.deps.org.apache.http.HttpRequest;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpEntityEnclosingRequest;

@Deprecated
@NotThreadSafe
public class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest
{
    private HttpEntity entity;
    private boolean consumed;
    
    public EntityEnclosingRequestWrapper(final HttpEntityEnclosingRequest request) throws ProtocolException {
        super(request);
        this.setEntity(request.getEntity());
    }
    
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    public void setEntity(final HttpEntity entity) {
        this.entity = ((entity != null) ? new EntityWrapper(entity) : null);
        this.consumed = false;
    }
    
    public boolean expectContinue() {
        final Header expect = this.getFirstHeader("Expect");
        return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
    }
    
    public boolean isRepeatable() {
        return this.entity == null || this.entity.isRepeatable() || !this.consumed;
    }
    
    class EntityWrapper extends HttpEntityWrapper
    {
        EntityWrapper(final HttpEntity entity) {
            super(entity);
        }
        
        public void consumeContent() throws IOException {
            EntityEnclosingRequestWrapper.this.consumed = true;
            super.consumeContent();
        }
        
        public InputStream getContent() throws IOException {
            EntityEnclosingRequestWrapper.this.consumed = true;
            return super.getContent();
        }
        
        public void writeTo(final OutputStream outstream) throws IOException {
            EntityEnclosingRequestWrapper.this.consumed = true;
            super.writeTo(outstream);
        }
    }
}

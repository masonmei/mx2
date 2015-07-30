// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.entity;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;

@NotThreadSafe
public class HttpEntityWrapper implements HttpEntity
{
    protected HttpEntity wrappedEntity;
    
    public HttpEntityWrapper(final HttpEntity wrappedEntity) {
        this.wrappedEntity = Args.notNull(wrappedEntity, "Wrapped entity");
    }
    
    public boolean isRepeatable() {
        return this.wrappedEntity.isRepeatable();
    }
    
    public boolean isChunked() {
        return this.wrappedEntity.isChunked();
    }
    
    public long getContentLength() {
        return this.wrappedEntity.getContentLength();
    }
    
    public Header getContentType() {
        return this.wrappedEntity.getContentType();
    }
    
    public Header getContentEncoding() {
        return this.wrappedEntity.getContentEncoding();
    }
    
    public InputStream getContent() throws IOException {
        return this.wrappedEntity.getContent();
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        this.wrappedEntity.writeTo(outstream);
    }
    
    public boolean isStreaming() {
        return this.wrappedEntity.isStreaming();
    }
    
    @Deprecated
    public void consumeContent() throws IOException {
        this.wrappedEntity.consumeContent();
    }
}

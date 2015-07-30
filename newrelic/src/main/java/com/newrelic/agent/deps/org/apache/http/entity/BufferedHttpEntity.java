// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.entity;

import com.newrelic.agent.deps.org.apache.http.util.Args;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.util.EntityUtils;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class BufferedHttpEntity extends HttpEntityWrapper
{
    private final byte[] buffer;
    
    public BufferedHttpEntity(final HttpEntity entity) throws IOException {
        super(entity);
        if (!entity.isRepeatable() || entity.getContentLength() < 0L) {
            this.buffer = EntityUtils.toByteArray(entity);
        }
        else {
            this.buffer = null;
        }
    }
    
    public long getContentLength() {
        if (this.buffer != null) {
            return this.buffer.length;
        }
        return super.getContentLength();
    }
    
    public InputStream getContent() throws IOException {
        if (this.buffer != null) {
            return new ByteArrayInputStream(this.buffer);
        }
        return super.getContent();
    }
    
    public boolean isChunked() {
        return this.buffer == null && super.isChunked();
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        if (this.buffer != null) {
            outstream.write(this.buffer);
        }
        else {
            super.writeTo(outstream);
        }
    }
    
    public boolean isStreaming() {
        return this.buffer == null && super.isStreaming();
    }
}

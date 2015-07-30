// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.entity;

import java.util.zip.GZIPOutputStream;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.newrelic.agent.deps.org.apache.http.message.BasicHeader;
import com.newrelic.agent.deps.org.apache.http.Header;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.entity.HttpEntityWrapper;

public class GzipCompressingEntity extends HttpEntityWrapper
{
    private static final String GZIP_CODEC = "gzip";
    
    public GzipCompressingEntity(final HttpEntity entity) {
        super(entity);
    }
    
    public Header getContentEncoding() {
        return new BasicHeader("Content-Encoding", "gzip");
    }
    
    public long getContentLength() {
        return -1L;
    }
    
    public boolean isChunked() {
        return true;
    }
    
    public InputStream getContent() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        final GZIPOutputStream gzip = new GZIPOutputStream(outstream);
        try {
            this.wrappedEntity.writeTo(gzip);
        }
        finally {
            gzip.close();
        }
    }
}

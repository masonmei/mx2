// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.entity;

import java.io.OutputStream;
import com.newrelic.agent.deps.org.apache.http.Header;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;

public class GzipDecompressingEntity extends DecompressingEntity
{
    public GzipDecompressingEntity(final HttpEntity entity) {
        super(entity);
    }
    
    InputStream decorate(final InputStream wrapped) throws IOException {
        return new GZIPInputStream(wrapped);
    }
    
    public Header getContentEncoding() {
        return null;
    }
    
    public long getContentLength() {
        return -1L;
    }
}

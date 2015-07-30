// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface HttpEntity
{
    boolean isRepeatable();
    
    boolean isChunked();
    
    long getContentLength();
    
    Header getContentType();
    
    Header getContentEncoding();
    
    InputStream getContent() throws IOException, IllegalStateException;
    
    void writeTo(OutputStream p0) throws IOException;
    
    boolean isStreaming();
    
    @Deprecated
    void consumeContent() throws IOException;
}

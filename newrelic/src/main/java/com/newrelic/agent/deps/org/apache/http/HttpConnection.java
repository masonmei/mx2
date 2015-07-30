// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.IOException;
import java.io.Closeable;

public interface HttpConnection extends Closeable
{
    void close() throws IOException;
    
    boolean isOpen();
    
    boolean isStale();
    
    void setSocketTimeout(int p0);
    
    int getSocketTimeout();
    
    void shutdown() throws IOException;
    
    HttpConnectionMetrics getMetrics();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.io;

import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import java.io.IOException;

public interface SessionInputBuffer
{
    int read(byte[] p0, int p1, int p2) throws IOException;
    
    int read(byte[] p0) throws IOException;
    
    int read() throws IOException;
    
    int readLine(CharArrayBuffer p0) throws IOException;
    
    String readLine() throws IOException;
    
    @Deprecated
    boolean isDataAvailable(int p0) throws IOException;
    
    HttpTransportMetrics getMetrics();
}

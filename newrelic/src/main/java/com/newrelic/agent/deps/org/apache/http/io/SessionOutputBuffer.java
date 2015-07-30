// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.io;

import com.newrelic.agent.deps.org.apache.http.util.CharArrayBuffer;
import java.io.IOException;

public interface SessionOutputBuffer
{
    void write(byte[] p0, int p1, int p2) throws IOException;
    
    void write(byte[] p0) throws IOException;
    
    void write(int p0) throws IOException;
    
    void writeLine(String p0) throws IOException;
    
    void writeLine(CharArrayBuffer p0) throws IOException;
    
    void flush() throws IOException;
    
    HttpTransportMetrics getMetrics();
}

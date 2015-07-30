// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import java.io.OutputStream;

@NotThreadSafe
class LoggingOutputStream extends OutputStream
{
    private final OutputStream out;
    private final Wire wire;
    
    public LoggingOutputStream(final OutputStream out, final Wire wire) {
        this.out = out;
        this.wire = wire;
    }
    
    public void write(final int b) throws IOException {
        this.wire.output(b);
    }
    
    public void write(final byte[] b) throws IOException {
        this.wire.output(b);
        this.out.write(b);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.wire.output(b, off, len);
        this.out.write(b, off, len);
    }
    
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public void close() throws IOException {
        this.out.close();
    }
}

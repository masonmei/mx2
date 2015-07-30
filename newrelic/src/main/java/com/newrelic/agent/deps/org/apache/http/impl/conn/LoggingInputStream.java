// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.conn;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import java.io.InputStream;

@NotThreadSafe
class LoggingInputStream extends InputStream
{
    private final InputStream in;
    private final Wire wire;
    
    public LoggingInputStream(final InputStream in, final Wire wire) {
        this.in = in;
        this.wire = wire;
    }
    
    public int read() throws IOException {
        final int b = this.in.read();
        if (b != -1) {
            this.wire.input(b);
        }
        return b;
    }
    
    public int read(final byte[] b) throws IOException {
        final int bytesRead = this.in.read(b);
        if (bytesRead != -1) {
            this.wire.input(b, 0, bytesRead);
        }
        return bytesRead;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytesRead = this.in.read(b, off, len);
        if (bytesRead != -1) {
            this.wire.input(b, off, bytesRead);
        }
        return bytesRead;
    }
    
    public long skip(final long n) throws IOException {
        return super.skip(n);
    }
    
    public int available() throws IOException {
        return this.in.available();
    }
    
    public synchronized void mark(final int readlimit) {
        super.mark(readlimit);
    }
    
    public synchronized void reset() throws IOException {
        super.reset();
    }
    
    public boolean markSupported() {
        return false;
    }
    
    public void close() throws IOException {
        this.in.close();
    }
}

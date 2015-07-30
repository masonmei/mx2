// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.io.SessionOutputBuffer;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import java.io.OutputStream;

@NotThreadSafe
public class IdentityOutputStream extends OutputStream
{
    private final SessionOutputBuffer out;
    private boolean closed;
    
    public IdentityOutputStream(final SessionOutputBuffer out) {
        this.closed = false;
        this.out = Args.notNull(out, "Session output buffer");
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.out.flush();
        }
    }
    
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        this.out.write(b, off, len);
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final int b) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted write to closed stream.");
        }
        this.out.write(b);
    }
}

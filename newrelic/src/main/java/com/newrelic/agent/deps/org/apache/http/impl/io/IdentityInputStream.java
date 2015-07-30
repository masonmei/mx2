// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.io.BufferInfo;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.io.SessionInputBuffer;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import java.io.InputStream;

@NotThreadSafe
public class IdentityInputStream extends InputStream
{
    private final SessionInputBuffer in;
    private boolean closed;
    
    public IdentityInputStream(final SessionInputBuffer in) {
        this.closed = false;
        this.in = Args.notNull(in, "Session input buffer");
    }
    
    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            return ((BufferInfo)this.in).length();
        }
        return 0;
    }
    
    public void close() throws IOException {
        this.closed = true;
    }
    
    public int read() throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.in.read();
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.closed) {
            return -1;
        }
        return this.in.read(b, off, len);
    }
}

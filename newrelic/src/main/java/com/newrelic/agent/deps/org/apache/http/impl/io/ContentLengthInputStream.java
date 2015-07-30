// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import com.newrelic.agent.deps.org.apache.http.ConnectionClosedException;
import com.newrelic.agent.deps.org.apache.http.io.BufferInfo;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.io.SessionInputBuffer;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import java.io.InputStream;

@NotThreadSafe
public class ContentLengthInputStream extends InputStream
{
    private static final int BUFFER_SIZE = 2048;
    private final long contentLength;
    private long pos;
    private boolean closed;
    private SessionInputBuffer in;
    
    public ContentLengthInputStream(final SessionInputBuffer in, final long contentLength) {
        this.pos = 0L;
        this.closed = false;
        this.in = null;
        this.in = Args.notNull(in, "Session input buffer");
        this.contentLength = Args.notNegative(contentLength, "Content length");
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            try {
                if (this.pos < this.contentLength) {
                    final byte[] buffer = new byte[2048];
                    while (this.read(buffer) >= 0) {}
                }
            }
            finally {
                this.closed = true;
            }
        }
    }
    
    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            final int len = ((BufferInfo)this.in).length();
            return Math.min(len, (int)(this.contentLength - this.pos));
        }
        return 0;
    }
    
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        final int b = this.in.read();
        if (b == -1) {
            if (this.pos < this.contentLength) {
                throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: " + this.contentLength + "; received: " + this.pos);
            }
        }
        else {
            ++this.pos;
        }
        return b;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.pos >= this.contentLength) {
            return -1;
        }
        int chunk = len;
        if (this.pos + len > this.contentLength) {
            chunk = (int)(this.contentLength - this.pos);
        }
        final int count = this.in.read(b, off, chunk);
        if (count == -1 && this.pos < this.contentLength) {
            throw new ConnectionClosedException("Premature end of Content-Length delimited message body (expected: " + this.contentLength + "; received: " + this.pos);
        }
        if (count > 0) {
            this.pos += count;
        }
        return count;
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        final byte[] buffer = new byte[2048];
        long remaining = Math.min(n, this.contentLength - this.pos);
        long count = 0L;
        while (remaining > 0L) {
            final int l = this.read(buffer, 0, (int)Math.min(2048L, remaining));
            if (l == -1) {
                break;
            }
            count += l;
            remaining -= l;
        }
        return count;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.io;

import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.params.HttpParams;
import java.net.Socket;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.io.EofSensor;

@Deprecated
@NotThreadSafe
public class SocketInputBuffer extends AbstractSessionInputBuffer implements EofSensor
{
    private final Socket socket;
    private boolean eof;
    
    public SocketInputBuffer(final Socket socket, final int buffersize, final HttpParams params) throws IOException {
        Args.notNull(socket, "Socket");
        this.socket = socket;
        this.eof = false;
        int n = buffersize;
        if (n < 0) {
            n = socket.getReceiveBufferSize();
        }
        if (n < 1024) {
            n = 1024;
        }
        this.init(socket.getInputStream(), n, params);
    }
    
    protected int fillBuffer() throws IOException {
        final int i = super.fillBuffer();
        this.eof = (i == -1);
        return i;
    }
    
    public boolean isDataAvailable(final int timeout) throws IOException {
        boolean result = this.hasBufferedData();
        if (!result) {
            final int oldtimeout = this.socket.getSoTimeout();
            try {
                this.socket.setSoTimeout(timeout);
                this.fillBuffer();
                result = this.hasBufferedData();
            }
            finally {
                this.socket.setSoTimeout(oldtimeout);
            }
        }
        return result;
    }
    
    public boolean isEof() {
        return this.eof;
    }
}

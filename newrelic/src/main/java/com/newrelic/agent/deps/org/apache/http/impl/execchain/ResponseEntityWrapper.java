// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.execchain;

import java.net.SocketException;
import java.io.OutputStream;
import com.newrelic.agent.deps.org.apache.http.conn.EofSensorInputStream;
import java.io.InputStream;
import java.io.IOException;
import com.newrelic.agent.deps.org.apache.http.HttpEntity;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;
import com.newrelic.agent.deps.org.apache.http.conn.EofSensorWatcher;
import com.newrelic.agent.deps.org.apache.http.entity.HttpEntityWrapper;

@NotThreadSafe
class ResponseEntityWrapper extends HttpEntityWrapper implements EofSensorWatcher
{
    private final ConnectionHolder connReleaseTrigger;
    
    public ResponseEntityWrapper(final HttpEntity entity, final ConnectionHolder connReleaseTrigger) {
        super(entity);
        this.connReleaseTrigger = connReleaseTrigger;
    }
    
    private void cleanup() {
        if (this.connReleaseTrigger != null) {
            this.connReleaseTrigger.abortConnection();
        }
    }
    
    public void releaseConnection() throws IOException {
        if (this.connReleaseTrigger != null) {
            try {
                if (this.connReleaseTrigger.isReusable()) {
                    this.connReleaseTrigger.releaseConnection();
                }
            }
            finally {
                this.cleanup();
            }
        }
    }
    
    public boolean isRepeatable() {
        return false;
    }
    
    public InputStream getContent() throws IOException {
        return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
    }
    
    @Deprecated
    public void consumeContent() throws IOException {
        this.releaseConnection();
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        try {
            this.wrappedEntity.writeTo(outstream);
            this.releaseConnection();
        }
        finally {
            this.cleanup();
        }
    }
    
    public boolean eofDetected(final InputStream wrapped) throws IOException {
        try {
            wrapped.close();
            this.releaseConnection();
        }
        finally {
            this.cleanup();
        }
        return false;
    }
    
    public boolean streamClosed(final InputStream wrapped) throws IOException {
        try {
            final boolean open = this.connReleaseTrigger != null && !this.connReleaseTrigger.isReleased();
            try {
                wrapped.close();
                this.releaseConnection();
            }
            catch (SocketException ex) {
                if (open) {
                    throw ex;
                }
            }
        }
        finally {
            this.cleanup();
        }
        return false;
    }
    
    public boolean streamAbort(final InputStream wrapped) throws IOException {
        this.cleanup();
        return false;
    }
}

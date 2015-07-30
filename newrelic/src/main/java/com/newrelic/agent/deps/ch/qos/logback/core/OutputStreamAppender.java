// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.DeferredProcessingAware;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import java.io.OutputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LogbackLock;
import com.newrelic.agent.deps.ch.qos.logback.core.encoder.Encoder;

public class OutputStreamAppender<E> extends UnsynchronizedAppenderBase<E>
{
    protected Encoder<E> encoder;
    protected LogbackLock lock;
    private OutputStream outputStream;
    
    public OutputStreamAppender() {
        this.lock = new LogbackLock();
    }
    
    public OutputStream getOutputStream() {
        return this.outputStream;
    }
    
    public void start() {
        int errors = 0;
        if (this.encoder == null) {
            this.addStatus(new ErrorStatus("No encoder set for the appender named \"" + this.name + "\".", this));
            ++errors;
        }
        if (this.outputStream == null) {
            this.addStatus(new ErrorStatus("No output stream set for the appender named \"" + this.name + "\".", this));
            ++errors;
        }
        if (errors == 0) {
            super.start();
        }
    }
    
    public void setLayout(final Layout<E> layout) {
        this.addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
        this.addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
        this.addWarn("See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details");
        final LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder<E>();
        lwe.setLayout(layout);
        lwe.setContext(this.context);
        this.encoder = lwe;
    }
    
    protected void append(final E eventObject) {
        if (!this.isStarted()) {
            return;
        }
        this.subAppend(eventObject);
    }
    
    public void stop() {
        synchronized (this.lock) {
            this.closeOutputStream();
            super.stop();
        }
    }
    
    protected void closeOutputStream() {
        if (this.outputStream != null) {
            try {
                this.encoderClose();
                this.outputStream.close();
                this.outputStream = null;
            }
            catch (IOException e) {
                this.addStatus(new ErrorStatus("Could not close output stream for OutputStreamAppender.", this, e));
            }
        }
    }
    
    void encoderInit() {
        if (this.encoder != null && this.outputStream != null) {
            try {
                this.encoder.init(this.outputStream);
            }
            catch (IOException ioe) {
                this.started = false;
                this.addStatus(new ErrorStatus("Failed to initialize encoder for appender named [" + this.name + "].", this, ioe));
            }
        }
    }
    
    void encoderClose() {
        if (this.encoder != null && this.outputStream != null) {
            try {
                this.encoder.close();
            }
            catch (IOException ioe) {
                this.started = false;
                this.addStatus(new ErrorStatus("Failed to write footer for appender named [" + this.name + "].", this, ioe));
            }
        }
    }
    
    public void setOutputStream(final OutputStream outputStream) {
        synchronized (this.lock) {
            this.closeOutputStream();
            this.outputStream = outputStream;
            if (this.encoder == null) {
                this.addWarn("Encoder has not been set. Cannot invoke its init method.");
                return;
            }
            this.encoderInit();
        }
    }
    
    protected void writeOut(final E event) throws IOException {
        this.encoder.doEncode(event);
    }
    
    protected void subAppend(final E event) {
        if (!this.isStarted()) {
            return;
        }
        try {
            if (event instanceof DeferredProcessingAware) {
                ((DeferredProcessingAware)event).prepareForDeferredProcessing();
            }
            synchronized (this.lock) {
                this.writeOut(event);
            }
        }
        catch (IOException ioe) {
            this.started = false;
            this.addStatus(new ErrorStatus("IO failure in appender", this, ioe));
        }
    }
    
    public Encoder<E> getEncoder() {
        return this.encoder;
    }
    
    public void setEncoder(final Encoder<E> encoder) {
        this.encoder = encoder;
    }
}

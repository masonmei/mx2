// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import com.newrelic.agent.deps.ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import com.newrelic.agent.deps.ch.qos.logback.core.Layout;

public class LayoutWrappingEncoder<E> extends EncoderBase<E>
{
    protected Layout<E> layout;
    private Charset charset;
    private boolean immediateFlush;
    
    public LayoutWrappingEncoder() {
        this.immediateFlush = true;
    }
    
    public void setImmediateFlush(final boolean immediateFlush) {
        this.immediateFlush = immediateFlush;
    }
    
    public boolean isImmediateFlush() {
        return this.immediateFlush;
    }
    
    public Layout<E> getLayout() {
        return this.layout;
    }
    
    public void setLayout(final Layout<E> layout) {
        this.layout = layout;
    }
    
    public Charset getCharset() {
        return this.charset;
    }
    
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }
    
    public void init(final OutputStream os) throws IOException {
        super.init(os);
        this.writeHeader();
    }
    
    void writeHeader() throws IOException {
        if (this.layout != null && this.outputStream != null) {
            final StringBuilder sb = new StringBuilder();
            this.appendIfNotNull(sb, this.layout.getFileHeader());
            this.appendIfNotNull(sb, this.layout.getPresentationHeader());
            if (sb.length() > 0) {
                sb.append(CoreConstants.LINE_SEPARATOR);
                this.outputStream.write(this.convertToBytes(sb.toString()));
                this.outputStream.flush();
            }
        }
    }
    
    public void close() throws IOException {
        this.writeFooter();
    }
    
    void writeFooter() throws IOException {
        if (this.layout != null && this.outputStream != null) {
            final StringBuilder sb = new StringBuilder();
            this.appendIfNotNull(sb, this.layout.getPresentationFooter());
            this.appendIfNotNull(sb, this.layout.getFileFooter());
            if (sb.length() > 0) {
                this.outputStream.write(this.convertToBytes(sb.toString()));
                this.outputStream.flush();
            }
        }
    }
    
    private byte[] convertToBytes(final String s) {
        if (this.charset == null) {
            return s.getBytes();
        }
        try {
            return s.getBytes(this.charset.name());
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("An existing charset cannot possibly be unsupported.");
        }
    }
    
    public void doEncode(final E event) throws IOException {
        final String txt = this.layout.doLayout(event);
        this.outputStream.write(this.convertToBytes(txt));
        if (this.immediateFlush) {
            this.outputStream.flush();
        }
    }
    
    public boolean isStarted() {
        return false;
    }
    
    public void start() {
        this.started = true;
    }
    
    public void stop() {
        this.started = false;
        if (this.outputStream != null) {
            try {
                this.outputStream.flush();
            }
            catch (IOException ex) {}
        }
    }
    
    private void appendIfNotNull(final StringBuilder sb, final String s) {
        if (s != null) {
            sb.append(s);
        }
    }
}

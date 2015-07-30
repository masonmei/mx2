// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.entity;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import com.newrelic.agent.deps.org.apache.http.protocol.HTTP;
import com.newrelic.agent.deps.org.apache.http.util.Args;
import com.newrelic.agent.deps.org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class StringEntity extends AbstractHttpEntity implements Cloneable
{
    protected final byte[] content;
    
    public StringEntity(final String string, final ContentType contentType) {
        Args.notNull(string, "Source string");
        Charset charset = (contentType != null) ? contentType.getCharset() : null;
        if (charset == null) {
            charset = HTTP.DEF_CONTENT_CHARSET;
        }
        try {
            this.content = string.getBytes(charset.name());
        }
        catch (UnsupportedEncodingException ex) {
            throw new UnsupportedCharsetException(charset.name());
        }
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    public StringEntity(final String string, final String mimeType, final String charset) throws UnsupportedEncodingException {
        Args.notNull(string, "Source string");
        final String mt = (mimeType != null) ? mimeType : "text/plain";
        final String cs = (charset != null) ? charset : "ISO-8859-1";
        this.content = string.getBytes(cs);
        this.setContentType(mt + "; charset=" + cs);
    }
    
    public StringEntity(final String string, final String charset) throws UnsupportedEncodingException {
        this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }
    
    public StringEntity(final String string, final Charset charset) {
        this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }
    
    public StringEntity(final String string) throws UnsupportedEncodingException {
        this(string, ContentType.DEFAULT_TEXT);
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public long getContentLength() {
        return this.content.length;
    }
    
    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }
    
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        outstream.write(this.content);
        outstream.flush();
    }
    
    public boolean isStreaming() {
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

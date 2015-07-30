// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.net;

import com.newrelic.agent.deps.org.apache.commons.codec.DecoderException;
import java.io.UnsupportedEncodingException;
import com.newrelic.agent.deps.org.apache.commons.codec.EncoderException;
import com.newrelic.agent.deps.org.apache.commons.codec.binary.Base64;
import com.newrelic.agent.deps.org.apache.commons.codec.StringDecoder;
import com.newrelic.agent.deps.org.apache.commons.codec.StringEncoder;

public class BCodec extends RFC1522Codec implements StringEncoder, StringDecoder
{
    private final String charset;
    
    public BCodec() {
        this("UTF-8");
    }
    
    public BCodec(final String charset) {
        this.charset = charset;
    }
    
    protected String getEncoding() {
        return "B";
    }
    
    protected byte[] doEncoding(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.encodeBase64(bytes);
    }
    
    protected byte[] doDecoding(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Base64.decodeBase64(bytes);
    }
    
    public String encode(final String value, final String charset) throws EncoderException {
        if (value == null) {
            return null;
        }
        try {
            return this.encodeText(value, charset);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncoderException(e.getMessage(), e);
        }
    }
    
    public String encode(final String value) throws EncoderException {
        if (value == null) {
            return null;
        }
        return this.encode(value, this.getDefaultCharset());
    }
    
    public String decode(final String value) throws DecoderException {
        if (value == null) {
            return null;
        }
        try {
            return this.decodeText(value);
        }
        catch (UnsupportedEncodingException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }
    
    public Object encode(final Object value) throws EncoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return this.encode((String)value);
        }
        throw new EncoderException("Objects of type " + value.getClass().getName() + " cannot be encoded using BCodec");
    }
    
    public Object decode(final Object value) throws DecoderException {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return this.decode((String)value);
        }
        throw new DecoderException("Objects of type " + value.getClass().getName() + " cannot be decoded using BCodec");
    }
    
    public String getDefaultCharset() {
        return this.charset;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.net;

import com.newrelic.agent.deps.org.apache.commons.codec.EncoderException;
import java.io.UnsupportedEncodingException;
import com.newrelic.agent.deps.org.apache.commons.codec.binary.StringUtils;
import com.newrelic.agent.deps.org.apache.commons.codec.DecoderException;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;
import com.newrelic.agent.deps.org.apache.commons.codec.StringDecoder;
import com.newrelic.agent.deps.org.apache.commons.codec.StringEncoder;
import com.newrelic.agent.deps.org.apache.commons.codec.BinaryDecoder;
import com.newrelic.agent.deps.org.apache.commons.codec.BinaryEncoder;

public class URLCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
{
    static final int RADIX = 16;
    protected String charset;
    protected static final byte ESCAPE_CHAR = 37;
    protected static final BitSet WWW_FORM_URL;
    
    public URLCodec() {
        this("UTF-8");
    }
    
    public URLCodec(final String charset) {
        this.charset = charset;
    }
    
    public static final byte[] encodeUrl(BitSet urlsafe, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (urlsafe == null) {
            urlsafe = URLCodec.WWW_FORM_URL;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int b : bytes) {
            final byte c = (byte)b;
            if (b < 0) {
                b += 256;
            }
            if (urlsafe.get(b)) {
                if (b == 32) {
                    b = 43;
                }
                buffer.write(b);
            }
            else {
                buffer.write(37);
                final char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
                final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                buffer.write(hex1);
                buffer.write(hex2);
            }
        }
        return buffer.toByteArray();
    }
    
    public static final byte[] decodeUrl(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; ++i) {
            final int b = bytes[i];
            if (b == 43) {
                buffer.write(32);
            }
            else {
                if (b == 37) {
                    try {
                        final int u = Utils.digit16(bytes[++i]);
                        final int l = Utils.digit16(bytes[++i]);
                        buffer.write((char)((u << 4) + l));
                        continue;
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        throw new DecoderException("Invalid URL encoding: ", e);
                    }
                }
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }
    
    public byte[] encode(final byte[] bytes) {
        return encodeUrl(URLCodec.WWW_FORM_URL, bytes);
    }
    
    public byte[] decode(final byte[] bytes) throws DecoderException {
        return decodeUrl(bytes);
    }
    
    public String encode(final String pString, final String charset) throws UnsupportedEncodingException {
        if (pString == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(this.encode(pString.getBytes(charset)));
    }
    
    public String encode(final String pString) throws EncoderException {
        if (pString == null) {
            return null;
        }
        try {
            return this.encode(pString, this.getDefaultCharset());
        }
        catch (UnsupportedEncodingException e) {
            throw new EncoderException(e.getMessage(), e);
        }
    }
    
    public String decode(final String pString, final String charset) throws DecoderException, UnsupportedEncodingException {
        if (pString == null) {
            return null;
        }
        return new String(this.decode(StringUtils.getBytesUsAscii(pString)), charset);
    }
    
    public String decode(final String pString) throws DecoderException {
        if (pString == null) {
            return null;
        }
        try {
            return this.decode(pString, this.getDefaultCharset());
        }
        catch (UnsupportedEncodingException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }
    
    public Object encode(final Object pObject) throws EncoderException {
        if (pObject == null) {
            return null;
        }
        if (pObject instanceof byte[]) {
            return this.encode((byte[])pObject);
        }
        if (pObject instanceof String) {
            return this.encode((String)pObject);
        }
        throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be URL encoded");
    }
    
    public Object decode(final Object pObject) throws DecoderException {
        if (pObject == null) {
            return null;
        }
        if (pObject instanceof byte[]) {
            return this.decode((byte[])pObject);
        }
        if (pObject instanceof String) {
            return this.decode((String)pObject);
        }
        throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be URL decoded");
    }
    
    public String getDefaultCharset() {
        return this.charset;
    }
    
    public String getEncoding() {
        return this.charset;
    }
    
    static {
        WWW_FORM_URL = new BitSet(256);
        for (int i = 97; i <= 122; ++i) {
            URLCodec.WWW_FORM_URL.set(i);
        }
        for (int i = 65; i <= 90; ++i) {
            URLCodec.WWW_FORM_URL.set(i);
        }
        for (int i = 48; i <= 57; ++i) {
            URLCodec.WWW_FORM_URL.set(i);
        }
        URLCodec.WWW_FORM_URL.set(45);
        URLCodec.WWW_FORM_URL.set(95);
        URLCodec.WWW_FORM_URL.set(46);
        URLCodec.WWW_FORM_URL.set(42);
        URLCodec.WWW_FORM_URL.set(32);
    }
}

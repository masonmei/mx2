// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.net;

import com.newrelic.agent.deps.org.apache.commons.codec.binary.StringUtils;
import java.io.UnsupportedEncodingException;
import com.newrelic.agent.deps.org.apache.commons.codec.EncoderException;
import com.newrelic.agent.deps.org.apache.commons.codec.DecoderException;
import java.io.ByteArrayOutputStream;
import java.util.BitSet;
import com.newrelic.agent.deps.org.apache.commons.codec.StringDecoder;
import com.newrelic.agent.deps.org.apache.commons.codec.StringEncoder;
import com.newrelic.agent.deps.org.apache.commons.codec.BinaryDecoder;
import com.newrelic.agent.deps.org.apache.commons.codec.BinaryEncoder;

public class QuotedPrintableCodec implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
{
    private final String charset;
    private static final BitSet PRINTABLE_CHARS;
    private static final byte ESCAPE_CHAR = 61;
    private static final byte TAB = 9;
    private static final byte SPACE = 32;
    
    public QuotedPrintableCodec() {
        this("UTF-8");
    }
    
    public QuotedPrintableCodec(final String charset) {
        this.charset = charset;
    }
    
    private static final void encodeQuotedPrintable(final int b, final ByteArrayOutputStream buffer) {
        buffer.write(61);
        final char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
        final char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
        buffer.write(hex1);
        buffer.write(hex2);
    }
    
    public static final byte[] encodeQuotedPrintable(BitSet printable, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        if (printable == null) {
            printable = QuotedPrintableCodec.PRINTABLE_CHARS;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int b : bytes) {
            final byte c = (byte)b;
            if (b < 0) {
                b += 256;
            }
            if (printable.get(b)) {
                buffer.write(b);
            }
            else {
                encodeQuotedPrintable(b, buffer);
            }
        }
        return buffer.toByteArray();
    }
    
    public static final byte[] decodeQuotedPrintable(final byte[] bytes) throws DecoderException {
        if (bytes == null) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; ++i) {
            final int b = bytes[i];
            if (b == 61) {
                try {
                    final int u = Utils.digit16(bytes[++i]);
                    final int l = Utils.digit16(bytes[++i]);
                    buffer.write((char)((u << 4) + l));
                    continue;
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid quoted-printable encoding", e);
                }
            }
            buffer.write(b);
        }
        return buffer.toByteArray();
    }
    
    public byte[] encode(final byte[] bytes) {
        return encodeQuotedPrintable(QuotedPrintableCodec.PRINTABLE_CHARS, bytes);
    }
    
    public byte[] decode(final byte[] bytes) throws DecoderException {
        return decodeQuotedPrintable(bytes);
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
        throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be quoted-printable encoded");
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
        throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be quoted-printable decoded");
    }
    
    public String getDefaultCharset() {
        return this.charset;
    }
    
    public String encode(final String pString, final String charset) throws UnsupportedEncodingException {
        if (pString == null) {
            return null;
        }
        return StringUtils.newStringUsAscii(this.encode(pString.getBytes(charset)));
    }
    
    static {
        PRINTABLE_CHARS = new BitSet(256);
        for (int i = 33; i <= 60; ++i) {
            QuotedPrintableCodec.PRINTABLE_CHARS.set(i);
        }
        for (int i = 62; i <= 126; ++i) {
            QuotedPrintableCodec.PRINTABLE_CHARS.set(i);
        }
        QuotedPrintableCodec.PRINTABLE_CHARS.set(9);
        QuotedPrintableCodec.PRINTABLE_CHARS.set(32);
    }
}

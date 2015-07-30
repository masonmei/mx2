// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.commons.codec.binary;

import com.newrelic.agent.deps.org.apache.commons.codec.EncoderException;
import java.io.UnsupportedEncodingException;
import com.newrelic.agent.deps.org.apache.commons.codec.DecoderException;
import com.newrelic.agent.deps.org.apache.commons.codec.BinaryDecoder;
import com.newrelic.agent.deps.org.apache.commons.codec.BinaryEncoder;

public class Hex implements BinaryEncoder, BinaryDecoder
{
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";
    private static final char[] DIGITS_LOWER;
    private static final char[] DIGITS_UPPER;
    private final String charsetName;
    
    public static byte[] decodeHex(final char[] data) throws DecoderException {
        final int len = data.length;
        if ((len & 0x1) != 0x0) {
            throw new DecoderException("Odd number of characters.");
        }
        final byte[] out = new byte[len >> 1];
        int f;
        for (int i = 0, j = 0; j < len; ++j, f |= toDigit(data[j], j), ++j, out[i] = (byte)(f & 0xFF), ++i) {
            f = toDigit(data[j], j) << 4;
        }
        return out;
    }
    
    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }
    
    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? Hex.DIGITS_LOWER : Hex.DIGITS_UPPER);
    }
    
    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        int i = 0;
        int j = 0;
        while (i < l) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0xF & data[i]];
            ++i;
        }
        return out;
    }
    
    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }
    
    protected static int toDigit(final char ch, final int index) throws DecoderException {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
    
    public Hex() {
        this.charsetName = "UTF-8";
    }
    
    public Hex(final String csName) {
        this.charsetName = csName;
    }
    
    public byte[] decode(final byte[] array) throws DecoderException {
        try {
            return decodeHex(new String(array, this.getCharsetName()).toCharArray());
        }
        catch (UnsupportedEncodingException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }
    
    public Object decode(final Object object) throws DecoderException {
        try {
            final char[] charArray = (object instanceof String) ? ((String)object).toCharArray() : object;
            return decodeHex(charArray);
        }
        catch (ClassCastException e) {
            throw new DecoderException(e.getMessage(), e);
        }
    }
    
    public byte[] encode(final byte[] array) {
        return StringUtils.getBytesUnchecked(encodeHexString(array), this.getCharsetName());
    }
    
    public Object encode(final Object object) throws EncoderException {
        try {
            final byte[] byteArray = (object instanceof String) ? ((String)object).getBytes(this.getCharsetName()) : object;
            return encodeHex(byteArray);
        }
        catch (ClassCastException e) {
            throw new EncoderException(e.getMessage(), e);
        }
        catch (UnsupportedEncodingException e2) {
            throw new EncoderException(e2.getMessage(), e2);
        }
    }
    
    public String getCharsetName() {
        return this.charsetName;
    }
    
    public String toString() {
        return super.toString() + "[charsetName=" + this.charsetName + "]";
    }
    
    static {
        DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}

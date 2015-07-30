// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.io.ByteArrayOutputStream;

public class ByteArrayUtil
{
    static void writeInt(final byte[] byteArray, final int offset, final int i) {
        for (int j = 0; j < 4; ++j) {
            final int shift = 24 - j * 8;
            byteArray[offset + j] = (byte)(i >>> shift);
        }
    }
    
    static void writeInt(final ByteArrayOutputStream baos, final int i) {
        for (int j = 0; j < 4; ++j) {
            final int shift = 24 - j * 8;
            baos.write((byte)(i >>> shift));
        }
    }
    
    static int readInt(final byte[] byteArray, final int offset) {
        int i = 0;
        for (int j = 0; j < 4; ++j) {
            final int shift = 24 - j * 8;
            i += (byteArray[offset + j] & 0xFF) << shift;
        }
        return i;
    }
    
    public static String toHexString(final byte[] ba) {
        final StringBuffer sbuf = new StringBuffer();
        for (final byte b : ba) {
            final String s = Integer.toHexString(b & 0xFF);
            if (s.length() == 1) {
                sbuf.append('0');
            }
            sbuf.append(s);
        }
        return sbuf.toString();
    }
    
    public static byte[] hexStringToByteArray(final String s) {
        final int len = s.length();
        final byte[] ba = new byte[len / 2];
        for (int i = 0; i < ba.length; ++i) {
            final int j = i * 2;
            final int t = Integer.parseInt(s.substring(j, j + 2), 16);
            final byte b = (byte)(t & 0xFF);
            ba[i] = b;
        }
        return ba;
    }
}

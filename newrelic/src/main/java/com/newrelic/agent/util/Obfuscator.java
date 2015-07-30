// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.io.UnsupportedEncodingException;
import com.newrelic.org.apache.axis.encoding.Base64;

public class Obfuscator
{
    public static String obfuscateNameUsingKey(final String name, final String key) throws UnsupportedEncodingException {
        final byte[] encodedBytes = name.getBytes("UTF-8");
        final byte[] keyBytes = key.getBytes();
        return Base64.encode(encode(encodedBytes, keyBytes));
    }
    
    private static byte[] encode(final byte[] bytes, final byte[] keyBytes) {
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] ^= keyBytes[i % keyBytes.length];
        }
        return bytes;
    }
    
    public static String deobfuscateNameUsingKey(final String name, final String key) throws UnsupportedEncodingException {
        final byte[] bytes = Base64.decode(name);
        final byte[] keyBytes = key.getBytes();
        return new String(encode(bytes, keyBytes), "UTF-8");
    }
}

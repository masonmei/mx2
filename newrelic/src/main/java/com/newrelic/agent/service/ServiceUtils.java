// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service;

import java.util.concurrent.atomic.AtomicInteger;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class ServiceUtils
{
    private static final int ROTATED_BIT_SHIFT = 31;
    private static final String PATH_HASH_SEPARATOR = ";";
    
    public static int calculatePathHash(final String appName, final String txName, final Integer optionalReferringPathHash) {
        final int referringPathHash = (optionalReferringPathHash == null) ? 0 : optionalReferringPathHash;
        final int rotatedReferringPathHash = referringPathHash << 1 | referringPathHash >>> 31;
        return rotatedReferringPathHash ^ getHash(appName, txName);
    }
    
    public static int reversePathHash(final String appName, final String txName, final Integer optionalReferringPathHash) {
        final int referringPathHash = (optionalReferringPathHash == null) ? 0 : optionalReferringPathHash;
        final int rotatedReferringPathHash = referringPathHash ^ getHash(appName, txName);
        return rotatedReferringPathHash >>> 1 | rotatedReferringPathHash << 31;
    }
    
    private static int getHash(final String appName, final String txName) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] digest = md.digest((appName + ";" + txName).getBytes("UTF-8"));
            final int fromBytes = (digest[12] & 0xFF) << 24 | (digest[13] & 0xFF) << 16 | (digest[14] & 0xFF) << 8 | (digest[15] & 0xFF);
            return fromBytes;
        }
        catch (NoSuchAlgorithmException e) {
            return 0;
        }
        catch (UnsupportedEncodingException e2) {
            return 0;
        }
    }
    
    public static String intToHexString(final int val) {
        return String.format("%08x", val);
    }
    
    public static int hexStringToInt(final String val) {
        return (int)Long.parseLong(val, 16);
    }
    
    public static void readMemoryBarrier(final AtomicInteger i) {
        if (i.get() == -1) {
            i.set(0);
        }
    }
    
    public static void writeMemoryBarrier(final AtomicInteger i) {
        i.incrementAndGet();
    }
}

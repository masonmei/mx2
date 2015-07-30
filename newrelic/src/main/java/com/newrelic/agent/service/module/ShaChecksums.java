// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.module;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.net.URL;

public class ShaChecksums
{
    public static String computeSha(final URL url) throws NoSuchAlgorithmException, IOException {
        final MessageDigest md = MessageDigest.getInstance("SHA1");
        InputStream inputStream = null;
        try {
            inputStream = EmbeddedJars.getInputStream(url);
            final DigestInputStream dis = new DigestInputStream(inputStream, md);
            final byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
            final byte[] mdbytes = md.digest();
            final StringBuffer sb = new StringBuffer(40);
            for (int i = 0; i < mdbytes.length; ++i) {
                sb.append(Integer.toString((mdbytes[i] & 0xFF) + 256, 16).substring(1));
            }
            final String string = sb.toString();
            if (null != inputStream) {
                inputStream.close();
            }
            return string;
        }
        finally {
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.module;

import java.util.jar.JarEntry;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.io.InputStream;
import java.net.URL;

public class EmbeddedJars
{
    private static final String EMBEDDED_JAR = ".jar!/";
    
    public static InputStream getInputStream(URL url) throws IOException {
        final int index = url.toExternalForm().indexOf(".jar!/");
        InputStream inputStream;
        if (index > 0) {
            final String path = url.toExternalForm().substring(index + ".jar!/".length());
            url = new URL(url.toExternalForm().substring(0, index) + ".jar");
            inputStream = url.openStream();
            final JarInputStream jarStream = new JarInputStream(inputStream);
            if (!readToEntry(jarStream, path)) {
                inputStream.close();
                throw new IOException("Unable to open stream for " + path + " in " + url.toExternalForm());
            }
            inputStream = jarStream;
        }
        else {
            inputStream = url.openStream();
        }
        return inputStream;
    }
    
    private static boolean readToEntry(final JarInputStream jarStream, final String path) throws IOException {
        JarEntry jarEntry = null;
        while ((jarEntry = jarStream.getNextJarEntry()) != null) {
            if (path.equals(jarEntry.getName())) {
                return true;
            }
        }
        return false;
    }
    
    static JarInputStream getJarInputStream(final URL url) throws IOException {
        final boolean isEmbedded = url.toExternalForm().contains(".jar!/");
        final InputStream stream = getInputStream(url);
        if (!isEmbedded && stream instanceof JarInputStream) {
            return (JarInputStream)stream;
        }
        return new JarInputStream(stream);
    }
}

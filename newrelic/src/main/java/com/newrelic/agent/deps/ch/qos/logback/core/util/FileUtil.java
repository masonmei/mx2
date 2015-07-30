// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

import java.net.URLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public class FileUtil
{
    public static URL fileToURL(final File file) {
        try {
            return file.toURI().toURL();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Unexpected exception on file [" + file + "]", e);
        }
    }
    
    public static boolean isParentDirectoryCreationRequired(final File file) {
        final File parent = file.getParentFile();
        return parent != null && !parent.exists();
    }
    
    public static boolean createMissingParentDirectories(final File file) {
        final File parent = file.getParentFile();
        if (parent == null) {
            throw new IllegalStateException(file + " should not have a null parent");
        }
        if (parent.exists()) {
            throw new IllegalStateException(file + " should not have existing parent directory");
        }
        return parent.mkdirs();
    }
    
    public static String resourceAsString(final ContextAware ca, final ClassLoader classLoader, final String resourceName) {
        final URL url = classLoader.getResource(resourceName);
        if (url == null) {
            ca.addError("Failed to find resource [" + resourceName + "]");
            return null;
        }
        InputStreamReader isr = null;
        try {
            final URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            isr = new InputStreamReader(urlConnection.getInputStream());
            final char[] buf = new char[128];
            final StringBuilder builder = new StringBuilder();
            int count = -1;
            while ((count = isr.read(buf, 0, buf.length)) != -1) {
                builder.append(buf, 0, count);
            }
            return builder.toString();
        }
        catch (IOException e) {
            ca.addError("Failled to open " + resourceName, e);
        }
        finally {
            if (isr != null) {
                try {
                    isr.close();
                }
                catch (IOException ex) {}
            }
        }
        return null;
    }
}

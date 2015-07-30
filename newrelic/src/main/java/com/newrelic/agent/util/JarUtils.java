// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.Iterator;
import com.newrelic.agent.Agent;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;
import java.io.File;
import java.util.Map;

public class JarUtils
{
    public static File createJarFile(final String prefix, final Map<String, byte[]> classes) throws IOException {
        return createJarFile(prefix, classes, null);
    }
    
    public static File createJarFile(final String prefix, final Map<String, byte[]> classes, Manifest manifest) throws IOException {
        final File file = File.createTempFile(prefix, ".jar");
        file.deleteOnExit();
        if (manifest == null) {
            manifest = new Manifest();
        }
        final JarOutputStream outStream = new JarOutputStream(new FileOutputStream(file), manifest);
        writeFilesToJarStream(classes, file, outStream);
        return file;
    }
    
    private static void writeFilesToJarStream(final Map<String, byte[]> classes, final File file, final JarOutputStream outStream) throws IOException {
        final Map<String, byte[]> resources = (Map<String, byte[]>)Maps.newHashMap();
        for (final Map.Entry<String, byte[]> entry : classes.entrySet()) {
            resources.put(Utils.getClassResourceName(entry.getKey()), entry.getValue());
        }
        try {
            addJarEntries(outStream, resources);
            outStream.close();
        }
        finally {
            outStream.close();
        }
        Agent.LOG.finer("Created " + file.getAbsolutePath());
    }
    
    public static void addJarEntries(final JarOutputStream jarStream, final Map<String, byte[]> files) throws IOException {
        for (final Map.Entry<String, byte[]> entry : files.entrySet()) {
            final JarEntry jarEntry = new JarEntry(entry.getKey());
            jarStream.putNextEntry(jarEntry);
            jarStream.write(entry.getValue());
            jarStream.closeEntry();
        }
    }
}

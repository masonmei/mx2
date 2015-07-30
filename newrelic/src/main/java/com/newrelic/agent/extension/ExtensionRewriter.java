// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import java.util.Set;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import com.newrelic.agent.util.Streams;
import java.io.ByteArrayInputStream;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Remapper;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.commons.RemappingClassAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.jar.JarFile;

public class ExtensionRewriter
{
    static final DependencyRemapper REMAPPER;
    
    public static byte[] rewrite(final JarFile jar) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final JarOutputStream jarOut = new JarOutputStream(out);
        boolean modified = false;
        try {
            final Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                final JarEntry entry = e.nextElement();
                final JarEntry newEntry = new JarEntry(entry.getName());
                InputStream inputStream = jar.getInputStream(entry);
                try {
                    if (entry.getName().endsWith(".class")) {
                        final ClassReader cr = new ClassReader(inputStream);
                        final ClassWriter writer = new ClassWriter(2);
                        final ClassVisitor cv = new RemappingClassAdapter(writer, ExtensionRewriter.REMAPPER);
                        cr.accept(cv, 4);
                        if (!ExtensionRewriter.REMAPPER.getRemappings().isEmpty()) {
                            modified = true;
                        }
                        inputStream.close();
                        inputStream = new ByteArrayInputStream(writer.toByteArray());
                    }
                    jarOut.putNextEntry(newEntry);
                    Streams.copy(inputStream, jarOut, inputStream.available());
                    jarOut.closeEntry();
                    inputStream.close();
                }
                finally {
                    jarOut.closeEntry();
                    inputStream.close();
                }
            }
            jarOut.close();
            jar.close();
            out.close();
        }
        finally {
            jarOut.close();
            jar.close();
            out.close();
        }
        return (byte[])(modified ? out.toByteArray() : null);
    }
    
    static {
        REMAPPER = new DependencyRemapper(ImmutableSet.of("com/newrelic/agent/deps/org/objectweb/asm/", "com/newrelic/agent/deps/com/google/", "com/newrelic/agent/deps/org/apache/commons/"));
    }
}

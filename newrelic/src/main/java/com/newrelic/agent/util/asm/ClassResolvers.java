// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.asm;

import java.util.Iterator;
import java.net.URL;
import java.util.Enumeration;
import java.io.BufferedInputStream;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.io.File;
import java.util.Collection;
import java.io.IOException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.bootstrap.EmbeddedJarFilesImpl;
import com.newrelic.agent.deps.com.google.common.collect.Lists;

public class ClassResolvers
{
    public static ClassResolver getEmbeddedJarsClassResolver() {
        final Collection<ClassResolver> resolvers = (Collection<ClassResolver>)Lists.newArrayList();
        for (final String name : EmbeddedJarFilesImpl.INSTANCE.getEmbeddedAgentJarFileNames()) {
            try {
                resolvers.add(getJarClassResolver(EmbeddedJarFilesImpl.INSTANCE.getJarFileInAgent(name)));
            }
            catch (IOException e) {
                Agent.LOG.log(Level.SEVERE, (Throwable)e, "Unable to load {0} : {1}", new Object[] { name, e.getMessage() });
            }
        }
        return getMultiResolver(resolvers);
    }
    
    public static ClassResolver getJarClassResolver(final File jarFile) throws IOException {
        final Set<String> classNames = (Set<String>)Sets.newHashSet();
        final JarFile jar = new JarFile(jarFile);
        try {
            final Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                final JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    classNames.add(jarEntry.getName());
                }
            }
            jar.close();
        }
        finally {
            jar.close();
        }
        return new ClassResolver() {
            public InputStream getClassResource(final String internalClassName) throws IOException {
                final String resourceName = internalClassName + ".class";
                if (classNames.contains(resourceName)) {
                    final JarFile jar = new JarFile(jarFile);
                    final JarEntry entry = jar.getJarEntry(resourceName);
                    return new BufferedInputStream(jar.getInputStream(entry)) {
                        public void close() throws IOException {
                            super.close();
                            jar.close();
                        }
                    };
                }
                return null;
            }
            
            public String toString() {
                return jarFile.getAbsolutePath();
            }
        };
    }
    
    public static ClassResolver getClassLoaderResolver(final ClassLoader classLoader) {
        return new ClassResolver() {
            public InputStream getClassResource(final String internalClassName) throws IOException {
                final URL resource = Utils.getClassResource(classLoader, internalClassName);
                return (resource == null) ? null : resource.openStream();
            }
        };
    }
    
    public static ClassResolver getMultiResolver(final ClassResolver... resolvers) {
        return new ClassResolver() {
            public InputStream getClassResource(final String internalClassName) throws IOException {
                for (final ClassResolver resolver : resolvers) {
                    final InputStream classResource = resolver.getClassResource(internalClassName);
                    if (classResource != null) {
                        return classResource;
                    }
                }
                return null;
            }
        };
    }
    
    public static ClassResolver getMultiResolver(final Collection<ClassResolver> resolvers) {
        return new ClassResolver() {
            public InputStream getClassResource(final String internalClassName) throws IOException {
                for (final ClassResolver resolver : resolvers) {
                    final InputStream classResource = resolver.getClassResource(internalClassName);
                    if (classResource != null) {
                        return classResource;
                    }
                }
                return null;
            }
        };
    }
}

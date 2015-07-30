// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import java.io.OutputStream;
import com.newrelic.agent.util.Streams;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import com.newrelic.agent.service.ServiceFactory;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.jar.Manifest;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.weaver.WeaveUtils;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.text.MessageFormat;
import java.util.HashMap;
import java.io.IOException;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.logging.Level;
import java.util.jar.JarFile;
import com.newrelic.agent.logging.IAgentLogger;
import java.util.Map;
import java.io.File;

public class JarExtension
{
    private final ClassLoader classloader;
    private final File file;
    private final Map<String, Extension> extensions;
    
    public static JarExtension create(final IAgentLogger logger, final ExtensionParsers extensionParsers, File file) throws IOException {
        final JarFile jar = new JarFile(file);
        String agentClass;
        try {
            agentClass = getAgentClass(jar.getManifest());
            if (null != agentClass) {
                logger.log(Level.FINE, "Detected agentmain class {0} in {1}", new Object[] { agentClass, file.getAbsolutePath() });
                final byte[] newBytes = ExtensionRewriter.rewrite(jar);
                if (null != newBytes) {
                    validateJar(newBytes);
                    file = writeTempJar(logger, file, newBytes);
                }
            }
            jar.close();
        }
        finally {
            jar.close();
        }
        final JarExtension ext = new JarExtension(logger, extensionParsers, file, new URLClassLoader(new URL[] { file.toURI().toURL() }, ClassLoader.getSystemClassLoader()), true);
        if (agentClass != null) {
            ext.invokeMainMethod(logger, agentClass);
        }
        return ext;
    }
    
    public static JarExtension create(final IAgentLogger logger, final ExtensionParsers extensionParsers, final String jarFileName) throws IOException {
        return new JarExtension(logger, extensionParsers, new File(jarFileName), ClassLoader.getSystemClassLoader(), false);
    }
    
    private JarExtension(final IAgentLogger logger, final ExtensionParsers extensionParsers, final File file, final ClassLoader classLoader, final boolean custom) throws IOException {
        this.extensions = new HashMap<String, Extension>();
        this.classloader = classLoader;
        this.file = file;
        final JarFile jarFile = new JarFile(file);
        logger.fine(MessageFormat.format(custom ? "Loading extension jar \"{0}\"" : "Loading built-in agent extensions", file.getAbsolutePath()));
        final Collection<JarEntry> entries = getExtensions(jarFile);
        for (final JarEntry entry : entries) {
            InputStream iStream = null;
            try {
                iStream = jarFile.getInputStream(entry);
                if (iStream != null) {
                    try {
                        final Extension extension = extensionParsers.getParser(entry.getName()).parse(classLoader, iStream, custom);
                        this.addExtension(extension);
                    }
                    catch (Exception ex) {
                        logger.severe(MessageFormat.format("Invalid extension file {0} : {1}", entry.getName(), ex.toString()));
                        logger.log(Level.FINER, ex.toString(), ex);
                    }
                }
                else {
                    logger.fine(MessageFormat.format("Unable to load extension resource \"{0}\"", entry.getName()));
                }
                if (iStream == null) {
                    continue;
                }
                try {
                    iStream.close();
                }
                catch (Exception ex2) {}
            }
            finally {
                if (iStream != null) {
                    try {
                        iStream.close();
                    }
                    catch (Exception ex3) {}
                }
            }
        }
    }
    
    public ClassLoader getClassloader() {
        return this.classloader;
    }
    
    public final Map<String, Extension> getExtensions() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends Extension>)this.extensions);
    }
    
    void addExtension(final Extension extension) {
        final Extension existing = this.extensions.get(extension.getName());
        if (existing == null || existing.getVersionNumber() < extension.getVersionNumber()) {
            this.extensions.put(extension.getName(), extension);
        }
    }
    
    private static Collection<JarEntry> getExtensions(final JarFile file) {
        final List<JarEntry> list = new ArrayList<JarEntry>();
        final Pattern pattern = Pattern.compile("^META-INF/extensions/(.*).(yml|xml)$");
        final Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final String name = entry.getName();
            if (pattern.matcher(name).matches()) {
                list.add(entry);
            }
        }
        return list;
    }
    
    public boolean isWeaveInstrumentation() {
        return isWeaveInstrumentation(this.file);
    }
    
    public static boolean isWeaveInstrumentation(final File file) {
        final Collection<String> classNames = getClassFileNames(file);
        if (!classNames.isEmpty()) {
            if (!file.exists()) {
                return false;
            }
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(file);
                for (final String fileName : classNames) {
                    final JarEntry jarEntry = jarFile.getJarEntry(fileName);
                    InputStream stream = null;
                    try {
                        stream = jarFile.getInputStream(jarEntry);
                        if (stream != null) {
                            final ClassReader reader = new ClassReader(stream);
                            if (WeaveUtils.isWeavedClass(reader)) {
                                final boolean b = true;
                                if (stream != null) {
                                    try {
                                        stream.close();
                                    }
                                    catch (IOException ex2) {}
                                }
                                if (jarFile != null) {
                                    try {
                                        jarFile.close();
                                    }
                                    catch (IOException ex3) {}
                                }
                                return b;
                            }
                        }
                        if (stream == null) {
                            continue;
                        }
                        try {
                            stream.close();
                        }
                        catch (IOException ex4) {}
                    }
                    catch (IOException e) {
                        Agent.LOG.log(Level.INFO, "Error processing " + fileName, e);
                        if (stream == null) {
                            continue;
                        }
                        try {
                            stream.close();
                        }
                        catch (IOException ex5) {}
                    }
                    finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            }
                            catch (IOException ex6) {}
                        }
                    }
                }
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (IOException ex7) {}
                }
            }
            catch (IOException ex) {
                Agent.LOG.log(Level.INFO, "Error processing extension jar " + file, ex);
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (IOException ex8) {}
                }
            }
            finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (IOException ex9) {}
                }
            }
        }
        return false;
    }
    
    public Collection<String> getClassFileNames() {
        return getClassFileNames(this.file);
    }
    
    public static Collection<String> getClassFileNames(final File file) {
        if (file.exists()) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(file);
                final Collection<String> classes = new ArrayList<String>();
                final Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        final String fileName = entry.getName();
                        try {
                            classes.add(fileName);
                        }
                        catch (Exception ex) {}
                    }
                }
                final Collection<String> collection = classes;
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (IOException ex2) {}
                }
                return collection;
            }
            catch (IOException e) {
                Agent.LOG.debug("Unable to read classes in " + file.getAbsolutePath() + ".  " + e.getMessage());
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (IOException ex3) {}
                }
            }
            finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    }
                    catch (IOException ex4) {}
                }
            }
        }
        return (Collection<String>)Collections.emptyList();
    }
    
    public Collection<Class<?>> getClasses() {
        final Collection<String> classNames = this.getClassFileNames();
        if (classNames.isEmpty()) {
            return (Collection<Class<?>>)Collections.emptyList();
        }
        final Collection<Class<?>> classes = (Collection<Class<?>>)Lists.newArrayList();
        for (String fileName : classNames) {
            final int index = fileName.indexOf(".class");
            fileName = fileName.substring(0, index);
            fileName = fileName.replace('/', '.');
            try {
                classes.add(this.classloader.loadClass(fileName));
            }
            catch (Exception ex) {}
        }
        return classes;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public String toString() {
        return this.file.getAbsolutePath();
    }
    
    private static String getAgentClass(final Manifest manifest) {
        for (final String attr : Arrays.asList("Agent-Class", "Premain-Class")) {
            final String agentClass = manifest.getMainAttributes().getValue(attr);
            if (null != agentClass) {
                return agentClass;
            }
        }
        return null;
    }
    
    private void invokeMainMethod(final IAgentLogger logger, final String agentClass) {
        try {
            final Class<?> clazz = this.classloader.loadClass(agentClass);
            logger.log(Level.FINE, "Invoking {0}.premain method", new Object[] { agentClass });
            final Method method = clazz.getDeclaredMethod("premain", String.class, Instrumentation.class);
            final String agentArgs = "";
            method.invoke(null, agentArgs, ServiceFactory.getClassTransformerService().getExtensionInstrumentation());
        }
        catch (ClassNotFoundException e) {
            logger.log(Level.INFO, "Unable to load {0}", new Object[] { agentClass });
            logger.log(Level.FINEST, (Throwable)e, e.getMessage(), new Object[0]);
        }
        catch (NoSuchMethodException e2) {
            logger.log(Level.INFO, "{0} has no premain method", new Object[] { agentClass });
            logger.log(Level.FINEST, (Throwable)e2, e2.getMessage(), new Object[0]);
        }
        catch (SecurityException e3) {
            logger.log(Level.INFO, "Unable to load {0}", new Object[] { agentClass });
            logger.log(Level.FINEST, (Throwable)e3, e3.getMessage(), new Object[0]);
        }
        catch (Exception e4) {
            logger.log(Level.INFO, "Unable to invoke {0}.premain", new Object[] { agentClass });
            logger.log(Level.FINEST, (Throwable)e4, e4.getMessage(), new Object[0]);
        }
    }
    
    private static File writeTempJar(final IAgentLogger logger, File file, final byte[] newBytes) throws IOException {
        final File original = file;
        file = File.createTempFile(file.getName(), ".jar");
        file.deleteOnExit();
        final FileOutputStream out = new FileOutputStream(file);
        try {
            Streams.copy(new ByteArrayInputStream(newBytes), out, newBytes.length);
            out.close();
        }
        finally {
            out.close();
        }
        logger.log(Level.FINER, "Rewriting {0} as {1}", new Object[] { original.getAbsolutePath(), file.getAbsolutePath() });
        return file;
    }
    
    private static void validateJar(final byte[] bytes) throws IOException {
    }
}

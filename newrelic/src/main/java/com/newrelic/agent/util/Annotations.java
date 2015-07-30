// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.Set;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Collections;
import com.newrelic.agent.deps.org.reflections.Configuration;
import com.newrelic.agent.deps.org.reflections.serializers.Serializer;
import com.newrelic.agent.deps.org.reflections.serializers.JsonSerializer;
import com.newrelic.agent.deps.org.reflections.util.ConfigurationBuilder;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import com.newrelic.agent.config.JarResource;
import java.io.IOException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import com.newrelic.agent.config.AgentJarHelper;
import java.util.regex.Pattern;
import java.util.Collection;
import com.newrelic.agent.deps.org.reflections.Reflections;

public class Annotations
{
    private static Reflections loaded;
    
    public static Collection<Class<?>> getAnnotationClasses(final Class<?> annotationClass, String packageSearchPath) {
        final String pointcutAnnotation = 'L' + annotationClass.getName().replace('.', '/') + ';';
        if (!packageSearchPath.endsWith("/")) {
            packageSearchPath += "/";
        }
        final Pattern pattern = Pattern.compile(packageSearchPath + "(.*).class");
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final JarResource agentJarFile = AgentJarHelper.getAgentJarResource();
        try {
            final Collection<String> fileNames = AgentJarHelper.findAgentJarFileNames(pattern);
            final Collection<Class<?>> classes = new ArrayList<Class<?>>(fileNames.size());
            for (final String fileName : fileNames) {
                final int size = (int)agentJarFile.getSize(fileName);
                final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
                try {
                    Streams.copy(agentJarFile.getInputStream(fileName), out, size, true);
                    final ClassReader cr = new ClassReader(out.toByteArray());
                    final ClassStructure structure = ClassStructure.getClassStructure(cr, 4);
                    final Collection<String> annotations = structure.getClassAnnotations().keySet();
                    if (!annotations.contains(pointcutAnnotation)) {
                        continue;
                    }
                    String className = fileName.replace('/', '.');
                    final int index = className.indexOf(".class");
                    if (index > 0) {
                        className = className.substring(0, index);
                    }
                    final Class<?> clazz = classLoader.loadClass(className);
                    classes.add(clazz);
                }
                catch (Exception e) {
                    Agent.LOG.log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
                }
            }
            final Collection<Class<?>> collection = classes;
            try {
                agentJarFile.close();
            }
            catch (IOException e2) {
                Agent.LOG.log(Level.FINEST, (Throwable)e2, e2.toString(), new Object[0]);
            }
            return collection;
        }
        finally {
            try {
                agentJarFile.close();
            }
            catch (IOException e2) {
                Agent.LOG.log(Level.FINEST, (Throwable)e2, e2.toString(), new Object[0]);
            }
        }
    }
    
    public static Collection<Class<?>> getAnnotationClassesFromManifest(final Class<? extends Annotation> annotationClass, String packageSearchPath) {
        if (Annotations.loaded == null) {
            final JarResource agentJarFile = AgentJarHelper.getAgentJarResource();
            try {
                final Reflections loader = new Reflections(new ConfigurationBuilder().setSerializer(new JsonSerializer()));
                Annotations.loaded = loader.collect(agentJarFile.getInputStream("newrelic-manifest.json"));
                try {
                    agentJarFile.close();
                }
                catch (IOException e) {
                    Agent.LOG.log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
                }
            }
            catch (Exception e2) {
                final Set<Class<?>> emptySet = Collections.emptySet();
                try {
                    agentJarFile.close();
                }
                catch (IOException e) {
                    Agent.LOG.log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
                }
                return emptySet;
            }
            finally {
                try {
                    agentJarFile.close();
                }
                catch (IOException e) {
                    Agent.LOG.log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
                }
            }
        }
        final Set<Class<?>> annotationClasses = Annotations.loaded.getTypesAnnotatedWith(annotationClass);
        packageSearchPath = packageSearchPath.replaceAll("/", ".");
        final Set<Class<?>> filteredAnnotationClasses = (Set<Class<?>>)Sets.newHashSetWithExpectedSize(annotationClasses.size());
        for (final Class<?> annotationClassValue : annotationClasses) {
            if (annotationClassValue.getName().startsWith(packageSearchPath)) {
                filteredAnnotationClasses.add(annotationClassValue);
            }
        }
        return filteredAnnotationClasses;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import java.util.Set;
import java.util.Collection;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.bootstrap.BootstrapAgent;
import java.lang.instrument.ClassDefinition;
import com.newrelic.agent.service.ServiceFactory;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.jar.JarFile;
import com.newrelic.agent.util.JarUtils;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.lang.instrument.Instrumentation;
import java.io.IOException;
import java.util.List;
import java.util.Map;

abstract class ClassAppender
{
    public abstract void appendClasses(final ClassLoader p0, final Map<String, byte[]> p1, final List<String> p2) throws IOException;
    
    public static ClassAppender getBootstrapClassAppender(final Instrumentation instrumentation) {
        return new ClassAppender() {
            public void appendClasses(final ClassLoader loader, final Map<String, byte[]> classBytesMap, final List<String> loadOrderHint) throws IOException {
                if (Agent.LOG.isFinestEnabled()) {
                    for (final Map.Entry<String, byte[]> entry : classBytesMap.entrySet()) {
                        Agent.LOG.log(Level.FINEST, "Appending '{0}' to bootstrap class loader", new Object[] { entry.getKey() });
                    }
                }
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(JarUtils.createJarFile("instrumentation", classBytesMap)));
            }
        };
    }
    
    public static ClassAppender getSystemClassAppender() {
        Method defineClassMethod;
        try {
            defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
            defineClassMethod.setAccessible(true);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return new ClassAppender() {
            public void appendClasses(final ClassLoader loader, final Map<String, byte[]> classBytesMap, final List<String> loadOrder) throws IOException {
                final List<ClassDefinition> existingClasses = this.loadClassesInTopologicalOrder(loader, classBytesMap, loadOrder);
                if (!existingClasses.isEmpty() && ServiceFactory.getAgent().getInstrumentation().isRedefineClassesSupported()) {
                    try {
                        Agent.LOG.log(Level.FINEST, "Trying to redefine {0} classes: {1}", new Object[] { existingClasses.size(), existingClasses });
                        ServiceFactory.getAgent().getInstrumentation().redefineClasses((ClassDefinition[])existingClasses.toArray(new ClassDefinition[0]));
                    }
                    catch (Exception e) {
                        throw new IOException(e);
                    }
                }
            }
            
            private List<ClassDefinition> loadClassesInTopologicalOrder(final ClassLoader loader, final Map<String, byte[]> classBytesMap, final List<String> loadOrderHint) throws IOException {
                final ProtectionDomain protectionDomain = BootstrapAgent.class.getProtectionDomain();
                final List<ClassDefinition> existingClasses = (List<ClassDefinition>)Lists.newArrayList();
                boolean continueLoading = true;
                final Map<String, byte[]> loadedClasses = (Map<String, byte[]>)Maps.newHashMap();
                Set<String> unloadedClasses = (Set<String>)Sets.newLinkedHashSet((Iterable<?>)loadOrderHint);
                if (classBytesMap.size() != unloadedClasses.size()) {
                    Agent.LOG.log(Level.FINEST, "loadOrderHint ( size {0} ) differs from classBytesMap ( size {1} )", new Object[] { unloadedClasses.size(), classBytesMap.size() });
                    unloadedClasses = (Set<String>)Sets.newHashSet((Iterable<?>)classBytesMap.keySet());
                }
                while (continueLoading && unloadedClasses.size() > 0) {
                    continueLoading = false;
                    for (final String classname : unloadedClasses) {
                        final byte[] classBytes = classBytesMap.get(classname);
                        if (null == classBytes) {
                            Agent.LOG.log(Level.FINEST, "Class in loadOrderHint {0} was not found in classBytesMap", new Object[] { classname });
                            unloadedClasses = (Set<String>)Sets.newHashSet((Iterable<?>)classBytesMap.keySet());
                            continueLoading = true;
                            break;
                        }
                        try {
                            defineClassMethod.invoke(loader, classname.replace('/', '.'), classBytes, 0, classBytes.length, protectionDomain);
                            continueLoading = true;
                            loadedClasses.put(classname, classBytes);
                        }
                        catch (Exception e) {
                            if (Agent.isDebugEnabled()) {
                                Utils.print(classBytes);
                            }
                            if (!(e.getCause() instanceof LinkageError)) {
                                throw new IOException(e);
                            }
                            final String errorMessage = e.getCause().getMessage();
                            if (errorMessage != null && errorMessage.contains("attempted  duplicate class definition")) {
                                try {
                                    Agent.LOG.log(Level.FINEST, "attempted to append existing class {0}", new Object[] { classname });
                                    final Class<?> theClass = loader.loadClass(Type.getObjectType(classname).getClassName());
                                    existingClasses.add(new ClassDefinition(theClass, classBytes));
                                }
                                catch (ClassNotFoundException e2) {
                                    Agent.LOG.log(Level.FINEST, (Throwable)e2, e2.getMessage(), new Object[0]);
                                    continue;
                                }
                            }
                            Agent.LOG.log(Level.FINEST, "Could not resolve {0} due to {1}", new Object[] { classname, e.getCause() });
                        }
                    }
                    unloadedClasses.removeAll(loadedClasses.keySet());
                }
                if (unloadedClasses.size() > 0) {
                    Agent.LOG.log(Level.FINEST, "Error resolving {0} classes: {1}", new Object[] { unloadedClasses.size(), unloadedClasses });
                }
                return existingClasses;
            }
        };
    }
}

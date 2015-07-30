// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import java.net.URL;
import com.newrelic.agent.util.asm.Utils;
import com.newrelic.agent.deps.com.google.common.base.Predicate;
import com.newrelic.agent.util.ClassUtils;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.tree.FieldNode;
import com.newrelic.api.agent.weaver.Weave;
import java.util.concurrent.ExecutionException;
import com.newrelic.agent.Agent;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Iterator;
import java.io.IOException;
import java.util.logging.Level;
import com.newrelic.api.agent.Logger;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.stats.StatsService;
import com.newrelic.agent.stats.StatsWorks;
import java.text.MessageFormat;
import com.newrelic.agent.service.ServiceFactory;
import java.util.List;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.cache.CacheBuilder;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import com.newrelic.agent.deps.com.google.common.cache.LoadingCache;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.com.google.common.cache.Cache;
import java.util.Set;
import java.util.Map;

public class Verifier
{
    private volatile Map<String, Set<MethodWithAccess>> referencedClassMethods;
    private volatile Map<String, Set<MethodWithAccess>> referencedInterfaceMethods;
    private final Cache<ClassLoader, Boolean> classLoaders;
    private final Map<Type, ClassStructure> resolvedClasses;
    private final LoadingCache<ClassLoader, AtomicInteger> classLoaderLocks;
    private final InstrumentationPackage instrumentationPackage;
    private final ClassStructureResolver classStructureResolver;
    private static final int CLASS_STRUCTURE_FLAGS = 7;
    
    public Verifier(final InstrumentationPackage instrumentationPackage) throws NoSuchMethodException, SecurityException {
        this(instrumentationPackage, new AgentClassStructureResolver());
    }
    
    public Verifier(final InstrumentationPackage instrumentationPackage, final ClassStructureResolver classStructureResolver) throws NoSuchMethodException, SecurityException {
        this.instrumentationPackage = instrumentationPackage;
        this.classStructureResolver = classStructureResolver;
        this.referencedClassMethods = Collections.emptyMap();
        this.referencedInterfaceMethods = Collections.emptyMap();
        this.classLoaders = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build();
        this.classLoaderLocks = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(1L, TimeUnit.MINUTES).build(new CacheLoader<ClassLoader, AtomicInteger>() {
            public AtomicInteger load(final ClassLoader key) throws Exception {
                return new AtomicInteger();
            }
        });
        this.resolvedClasses = Maps.newConcurrentMap();
    }
    
    public Map<Type, ClassStructure> getResolvedClasses() {
        return Collections.unmodifiableMap(this.resolvedClasses);
    }
    
    public String getImplementationTitle() {
        return this.instrumentationPackage.getImplementationTitle();
    }
    
    public boolean isEnabled(final ClassLoader loader) {
        final Boolean enabled = this.classLoaders.getIfPresent(loader);
        return enabled == null || enabled;
    }
    
    public boolean isVerified(final ClassLoader loader) {
        final Boolean verified = this.isVerifiedObject(loader);
        return verified != null && verified;
    }
    
    private Boolean isVerifiedObject(final ClassLoader loader) {
        return this.classLoaders.getIfPresent(loader);
    }
    
    public boolean verify(final ClassAppender classAppender, final ClassLoader loader, final Map<String, byte[]> classesInNewJar, final List<String> newClassLoadOrder) {
        Boolean verified = this.isVerifiedObject(loader);
        if (verified != null) {
            return verified;
        }
        verified = this.doVerify(classAppender, loader, classesInNewJar, newClassLoadOrder);
        if (verified == null) {
            return this.isVerified(loader);
        }
        this.classLoaders.put(loader, verified);
        if (verified) {
            this.instrumentationPackage.getLogger().debug("Loading " + this.getImplementationTitle() + " instrumentation");
        }
        final StatsService statsService = ServiceFactory.getStatsService();
        statsService.doStatsWork(StatsWorks.getRecordMetricWork(MessageFormat.format(verified ? "Supportability/WeaveInstrumentation/Loaded/{0}/{1}" : "Supportability/WeaveInstrumentation/Skipped/{0}/{1}", this.getImplementationTitle(), this.instrumentationPackage.getImplementationVersion()), 1.0f));
        return verified;
    }
    
    private static void resolve(final IAgentLogger logger, final ClassStructureResolver classStructureResolver, final Map<String, Set<MethodWithAccess>> allReferenced, final ClassLoader loader, final Map<String, ClassStructure> resolvedClasses, final Set<String> unresolvedClasses, final boolean isInterface) {
        for (final Map.Entry<String, Set<MethodWithAccess>> entry : allReferenced.entrySet()) {
            final String internalName = entry.getKey();
            ClassStructure classStructure = null;
            try {
                classStructure = classStructureResolver.getClassStructure((Logger)logger, loader, internalName, 7);
            }
            catch (IOException e) {
                logger.log(Level.FINEST, (Throwable)e, "Error fetching class structure of {0} : {1}", new Object[] { internalName, e.getMessage() });
            }
            if (classStructure == null) {
                unresolvedClasses.add(internalName);
            }
            else if (!entry.getValue().isEmpty() && isInterface(classStructure.getAccess()) != isInterface) {
                unresolvedClasses.add(internalName);
                logger.finer(internalName + " is referenced as a" + (isInterface ? "n interface" : " class"));
            }
            else {
                resolvedClasses.put(internalName, classStructure);
            }
        }
    }
    
    private static boolean isInterface(final int access) {
        return (access & 0x200) != 0x0;
    }
    
    private Boolean doVerify(final ClassAppender classAppender, final ClassLoader loader, final Map<String, byte[]> classesInNewJar, final List<String> newClassLoadOrder) {
        final Map<String, ClassStructure> resolvedClasses = Maps.newHashMap();
        final Set<String> unresolvedClasses = Sets.newHashSet();
        this.resolveWeaveClasses(loader, unresolvedClasses);
        if (!unresolvedClasses.isEmpty()) {
            this.instrumentationPackage.getLogger().finer("Skipping " + this.getImplementationTitle() + " instrumentation.  Unresolved classes: " + unresolvedClasses);
            return false;
        }
        if (!this.instrumentationPackage.getSkipClasses().isEmpty()) {
            this.instrumentationPackage.getLogger().finest("Checking for the presence of classes: " + this.instrumentationPackage.getSkipClasses());
        }
        if (this.shouldSkip(loader)) {
            return false;
        }
        resolve(this.instrumentationPackage.getLogger(), this.classStructureResolver, this.referencedClassMethods, loader, resolvedClasses, unresolvedClasses, false);
        resolve(this.instrumentationPackage.getLogger(), this.classStructureResolver, this.referencedInterfaceMethods, loader, resolvedClasses, unresolvedClasses, true);
        unresolvedClasses.removeAll(resolvedClasses.keySet());
        final Map<String, Set<MethodWithAccess>> allReferenced = Maps.newHashMap(this.referencedClassMethods);
        allReferenced.putAll(this.referencedInterfaceMethods);
        final Set<String> set = Sets.newHashSet(unresolvedClasses);
        set.removeAll(classesInNewJar.keySet());
        if (!set.isEmpty()) {
            this.instrumentationPackage.getLogger().finer("Skipping " + this.getImplementationTitle() + " instrumentation.  Unresolved classes: " + set);
            return false;
        }
        for (final Map.Entry<String, ClassStructure> entry : resolvedClasses.entrySet()) {
            try {
                final Set<MethodWithAccess> methods = allReferenced.get(entry.getKey());
                if (methods.isEmpty()) {
                    continue;
                }
                this.verifyMethods(loader, methods, entry.getValue());
                if (!methods.isEmpty()) {
                    this.instrumentationPackage.getLogger().finer("Skipping " + this.getImplementationTitle() + " instrumentation.  " + entry.getKey() + " unresolved methods: " + methods);
                    return false;
                }
                continue;
            }
            catch (IOException ex) {
                this.instrumentationPackage.getLogger().log(Level.FINER, "Verifier error", ex);
            }
        }
        final Map<String, byte[]> copy = Maps.newHashMap(classesInNewJar);
        copy.keySet().retainAll(unresolvedClasses);
        if (!copy.isEmpty()) {
            try {
                final AtomicInteger lockCount = this.classLoaderLocks.get(loader);
                if (lockCount.getAndIncrement() == 0) {
                    try {
                        this.loadClasses(classAppender, loader, copy, newClassLoadOrder);
                    }
                    catch (Exception e) {
                        this.instrumentationPackage.getLogger().log(Level.FINEST, "Error loading unresolved clases: " + copy, e);
                        return null;
                    }
                }
            }
            catch (ExecutionException ex2) {
                Agent.LOG.log(Level.FINEST, (Throwable)ex2, ex2.toString(), new Object[0]);
                return this.isVerifiedObject(loader);
            }
        }
        return true;
    }
    
    private boolean shouldSkip(final ClassLoader loader) {
        for (final String className : this.instrumentationPackage.getSkipClasses()) {
            ClassStructure classStructure = null;
            try {
                classStructure = this.getClassStructure((Logger)this.instrumentationPackage.getLogger(), loader, className);
            }
            catch (IOException ex) {}
            if (classStructure != null) {
                this.instrumentationPackage.getLogger().log(Level.FINER, "Skipping weave package because {0} is present", new Object[] { className });
                return true;
            }
        }
        return false;
    }
    
    private void resolveWeaveClasses(final ClassLoader loader, final Set<String> unresolvedClasses) {
        for (final Map.Entry<String, WeavedClassInfo> entry : this.instrumentationPackage.getWeaveClasses().entrySet()) {
            final String internalName = entry.getKey();
            ClassStructure classStructure = null;
            try {
                classStructure = this.getClassStructure((Logger)this.instrumentationPackage.getLogger(), loader, internalName);
            }
            catch (IOException e) {
                this.instrumentationPackage.getLogger().log(Level.WARNING, "Could not resolved class structure for {0}", new Object[] { internalName });
            }
            if (classStructure == null || classStructure.getClassAnnotations().containsKey(Type.getDescriptor(Weave.class))) {
                unresolvedClasses.add(internalName);
            }
            else {
                final Collection<FieldNode> referencedFields = entry.getValue().getReferencedFields();
                for (final FieldNode field : referencedFields) {
                    final FieldNode fieldNode = classStructure.getFields().get(field.name);
                    if (fieldNode == null) {
                        unresolvedClasses.add(internalName);
                        this.instrumentationPackage.getLogger().finer("Field " + field.name + " does not exist on " + internalName);
                    }
                    else {
                        if (fieldNode.desc.equals(field.desc)) {
                            continue;
                        }
                        this.instrumentationPackage.getLogger().finer("Expected field " + field.name + " on " + internalName + " to have the signature " + field.desc + ", but found " + fieldNode.desc);
                    }
                }
            }
        }
    }
    
    private ClassStructure getClassStructure(final Logger logger, final ClassLoader loader, final String internalName) throws IOException {
        return this.classStructureResolver.getClassStructure(logger, loader, internalName, 7);
    }
    
    private void verifyMethods(final ClassLoader loader, final Set<MethodWithAccess> methods, final ClassStructure classStructure) throws IOException {
        this.resolvedClasses.put(classStructure.getType(), classStructure);
        final Set<Method> classStructureMethods = classStructure.getMethods();
        final Set<MethodWithAccess> methodsInClassStructure = Sets.newHashSet();
        for (final MethodWithAccess methodWithAccess : methods) {
            final Method method = methodWithAccess.getMethod();
            if (classStructureMethods.contains(method) && classStructure.isStatic(method) == methodWithAccess.isStatic()) {
                methodsInClassStructure.add(methodWithAccess);
            }
        }
        methods.removeAll(methodsInClassStructure);
        if (methods.isEmpty()) {
            return;
        }
        for (final String interfaceClass : classStructure.getInterfaces()) {
            this.verifyMethods(loader, methods, this.getClassStructure((Logger)this.instrumentationPackage.getLogger(), loader, interfaceClass));
            if (methods.isEmpty()) {
                return;
            }
        }
        final String superName = classStructure.getSuperName();
        if (superName != null) {
            this.verifyMethods(loader, methods, this.getClassStructure((Logger)this.instrumentationPackage.getLogger(), loader, superName));
        }
    }
    
    private void loadClasses(final ClassAppender classAppender, final ClassLoader loader, final Map<String, byte[]> classBytes, final List<String> newClassLoadOrder) throws IOException {
        final List<String> loadedClasses = Lists.newArrayList();
        for (final Map.Entry<String, byte[]> nameAndBytes : classBytes.entrySet()) {
            try {
                final Class<?> clazz = loader.loadClass(Type.getObjectType(nameAndBytes.getKey()).getClassName());
                if (clazz.getClassLoader() != null && !clazz.getClassLoader().equals(loader) && !this.isFullyResolveable(loader, clazz, nameAndBytes.getValue(), classBytes.keySet())) {
                    continue;
                }
                loadedClasses.add(Type.getInternalName(clazz));
            }
            catch (Exception ex) {}
        }
        if (!loadedClasses.isEmpty()) {
            this.instrumentationPackage.getLogger().finer(this.getImplementationTitle() + " skipping already loaded classes: " + loadedClasses);
            classBytes.keySet().removeAll(loadedClasses);
        }
        if (!classBytes.isEmpty()) {
            this.instrumentationPackage.getLogger().finer(this.getImplementationTitle() + " loading classes: " + classBytes.keySet() + " using class loader " + loader);
            classAppender.appendClasses(loader, classBytes, newClassLoadOrder);
        }
    }
    
    private boolean isFullyResolveable(final ClassLoader loader, final Class<?> clazz, final byte[] classBytes, final Set<String> newClassNames) {
        Set<String> referencedClasses = ClassUtils.getClassReferences(classBytes);
        referencedClasses.removeAll(newClassNames);
        referencedClasses = Sets.filter(referencedClasses, new Predicate<String>() {
            public boolean apply(final String internalClassName) {
                return !internalClassName.startsWith("java/");
            }
        });
        for (final String internalClassName : referencedClasses) {
            try {
                final String className = Type.getObjectType(internalClassName).getClassName();
                final Class<?> throughLoader = loader.loadClass(className);
                final Class<?> throughClassLoader = clazz.getClassLoader().loadClass(className);
                if (throughLoader != throughClassLoader && (!throughLoader.isAssignableFrom(throughClassLoader) || !throughClassLoader.isAssignableFrom(throughLoader))) {
                    this.instrumentationPackage.getLogger().log(Level.FINEST, "{0} was resolved through class loader {1}, but it references {2} and the version of that class loaded through {3} differs from the one loaded through {4}", new Object[] { clazz.getName(), clazz.getClassLoader(), className, loader, throughClassLoader.getClassLoader() });
                    return false;
                }
                continue;
            }
            catch (ClassNotFoundException ex) {}
        }
        return true;
    }
    
    public ClassStructure getClassStructure(final Type type) {
        ClassStructure classStructure = this.getResolvedClasses().get(type);
        if (classStructure == null) {
            for (final Map.Entry<ClassLoader, Boolean> entry : this.classLoaders.asMap().entrySet()) {
                if (entry.getValue()) {
                    final URL resource = entry.getKey().getResource(Utils.getClassResourceName(type.getInternalName()));
                    if (resource == null) {
                        continue;
                    }
                    try {
                        classStructure = ClassStructure.getClassStructure(resource);
                    }
                    catch (IOException e) {
                        this.instrumentationPackage.getLogger().finest("Unable to load structure of " + type.getClassName());
                    }
                }
            }
        }
        return classStructure;
    }
    
    void setReferences(final Map<String, Set<MethodWithAccess>> referencedClassMethods, final Map<String, Set<MethodWithAccess>> referencedInterfaceMethods) {
        this.referencedClassMethods = Collections.unmodifiableMap(referencedClassMethods);
        this.referencedInterfaceMethods = Collections.unmodifiableMap(referencedInterfaceMethods);
    }
}

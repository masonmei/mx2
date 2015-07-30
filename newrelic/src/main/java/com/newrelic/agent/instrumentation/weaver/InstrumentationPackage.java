// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.DefaultClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.io.ByteArrayInputStream;
import com.newrelic.agent.util.asm.PatchedClassWriter;
import com.newrelic.agent.util.asm.ClassResolvers;
import com.newrelic.agent.config.Config;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import java.io.IOException;
import java.util.Collections;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.util.BootstrapLoader;
import java.util.logging.Level;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import java.net.URL;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Remapper;
import com.newrelic.agent.deps.org.objectweb.asm.commons.RemappingClassAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.commons.SimpleRemapper;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InnerClassNode;
import com.newrelic.api.agent.weaver.MatchType;
import java.util.jar.JarEntry;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import com.newrelic.agent.util.Streams;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.jar.JarInputStream;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import java.io.Closeable;
import java.util.Collection;
import java.lang.instrument.Instrumentation;
import java.util.Set;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;
import com.newrelic.agent.util.asm.ClassResolver;

public class InstrumentationPackage implements ClassResolver
{
    final String implementationTitle;
    private final Verifier verifier;
    private final Map<Method, String> abstractMethods;
    private final Map<String, byte[]> classNames;
    private final Map<String, WeavedClassInfo> weaveClasses;
    private final Map<String, WeavedClassInfo> instrumentationInfo;
    final Map<String, byte[]> newClasses;
    public final List<String> newClassLoadOrder;
    private final Set<String> skipClasses;
    private final boolean containsBootstrapMergeClasses;
    private final ClassAppender classAppender;
    private final InstrumentationMetadata metaData;
    private final Instrumentation instrumentation;
    private final Collection<Closeable> closeables;
    private OptimizedClassMatcherBuilder matcherBuilder;
    private final ClassMatchVisitorFactory matcher;
    final float implementationVersion;
    private final String location;
    private final IAgentLogger logger;
    
    public InstrumentationPackage(final Instrumentation instrumentation, final IAgentLogger logger, final InstrumentationMetadata metaData, final JarInputStream jarStream) throws Exception {
        this.abstractMethods = (Map<Method, String>)Maps.newHashMap();
        this.weaveClasses = (Map<String, WeavedClassInfo>)Maps.newHashMap();
        this.instrumentationInfo = (Map<String, WeavedClassInfo>)Maps.newHashMap();
        this.newClasses = (Map<String, byte[]>)Maps.newHashMap();
        this.newClassLoadOrder = (List<String>)Lists.newLinkedList();
        this.skipClasses = (Set<String>)Sets.newHashSet();
        this.closeables = new ConcurrentLinkedQueue<Closeable>();
        this.matcherBuilder = OptimizedClassMatcherBuilder.newBuilder();
        this.location = metaData.getLocation();
        this.instrumentation = instrumentation;
        final Map<String, byte[]> classBytes = (Map<String, byte[]>)Maps.newHashMap();
        final Map<String, InstrumentationClassVisitor> instrumentationClasses = (Map<String, InstrumentationClassVisitor>)Maps.newHashMap();
        this.implementationTitle = metaData.getImplementationTitle();
        this.metaData = metaData;
        this.logger = logger;
        this.implementationVersion = metaData.getImplementationVersion();
        this.verifier = new Verifier(this);
        JarEntry entry = null;
        while ((entry = jarStream.getNextJarEntry()) != null) {
            if (entry.getName().endsWith(".class")) {
                final byte[] bytes = Streams.read(jarStream, (int)entry.getSize(), false);
                final InstrumentationClassVisitor instrumentationClass = InstrumentationClassVisitor.getInstrumentationClass(this, bytes);
                instrumentationClasses.put(instrumentationClass.getClassName(), instrumentationClass);
                final MatchType matchType = instrumentationClass.getMatchType();
                if (matchType != null) {
                    this.weaveClasses.put(instrumentationClass.getClassName(), instrumentationClass);
                }
                this.instrumentationInfo.put(instrumentationClass.getClassName(), instrumentationClass);
                logger.finest("Weave instrumentation class: " + instrumentationClass.getClassName() + ", type: " + ((matchType == null) ? "NewClass" : matchType));
                classBytes.put(instrumentationClass.getClassName(), bytes);
            }
        }
        this.newClassLoadOrder.addAll(instrumentationClasses.keySet());
        this.newClassLoadOrder.removeAll(this.weaveClasses.keySet());
        if (this.weaveClasses.isEmpty()) {
            logger.finer(this.implementationTitle + " does not contain any weaved classes.");
        }
        InstrumentationClassVisitor.performSecondPassProcessing(this, instrumentationClasses, this.weaveClasses, classBytes, this.newClassLoadOrder);
        this.classNames = (Map<String, byte[]>)ImmutableMap.copyOf((Map<?, ?>)this.performThirdPassProcessing(classBytes, instrumentationClasses));
        this.containsBootstrapMergeClasses = this.isBootstrapClassName(this.weaveClasses.keySet());
        if (this.containsBootstrapMergeClasses) {
            this.classAppender = ClassAppender.getBootstrapClassAppender(instrumentation);
        }
        else {
            this.classAppender = ClassAppender.getSystemClassAppender();
        }
        this.matcher = this.matcherBuilder.build();
        this.matcherBuilder = null;
    }
    
    private Map<String, byte[]> performThirdPassProcessing(final Map<String, byte[]> classBytes, final Map<String, InstrumentationClassVisitor> instrumentationClasses) {
        final Map<String, String> renamedClasses = (Map<String, String>)Maps.newHashMap();
        for (final InstrumentationClassVisitor instrumentationClass : instrumentationClasses.values()) {
            if (instrumentationClass.isWeaveInstrumentation()) {
                for (final InnerClassNode innerClass : instrumentationClass.innerClasses) {
                    final InstrumentationClassVisitor innerClassInfo = instrumentationClasses.get(innerClass.name);
                    if (innerClassInfo != null && !innerClassInfo.isWeaveInstrumentation()) {
                        renamedClasses.put(innerClass.name, innerClass.name + "$NR");
                    }
                }
            }
        }
        return this.renameClasses(classBytes, renamedClasses, instrumentationClasses);
    }
    
    private Map<String, byte[]> renameClasses(final Map<String, byte[]> classBytes, final Map<String, String> classesToRename, final Map<String, InstrumentationClassVisitor> instrumentationClasses) {
        final Map<String, byte[]> actualClassNames = (Map<String, byte[]>)Maps.newHashMap();
        final Map<String, Set<MethodWithAccess>> referencedClassMethods = (Map<String, Set<MethodWithAccess>>)Maps.newHashMap();
        final Map<String, Set<MethodWithAccess>> referencedInterfaceMethods = (Map<String, Set<MethodWithAccess>>)Maps.newHashMap();
        final Remapper remapper = new SimpleRemapper(classesToRename);
        for (final Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
            final ClassReader reader = new ClassReader(entry.getValue());
            ClassVisitor cv;
            final ClassWriter writer = (ClassWriter)(cv = new ClassWriter(1));
            final WeavedClassInfo instrumentationClass = this.instrumentationInfo.get(reader.getClassName());
            final boolean isWeaveClass = instrumentationClass != null && instrumentationClass.getMatchType() != null;
            if (isWeaveClass) {
                cv = new GatherClassMethodMatchers(cv, reader.getClassName(), instrumentationClass);
            }
            cv = new ReferencesVisitor(this.logger, this.getWeavedClassDetails(reader.getClassName()), cv, referencedClassMethods, referencedInterfaceMethods);
            if (!classesToRename.isEmpty()) {
                cv = new RemappingClassAdapter(cv, remapper);
            }
            reader.accept(cv, 8);
            String className = classesToRename.get(entry.getKey());
            if (className == null) {
                className = entry.getKey();
            }
            actualClassNames.put(className, writer.toByteArray());
            if (instrumentationClass != null && instrumentationClass.isSkipIfPresent()) {
                this.skipClasses.add(className);
            }
            else {
                if (isWeaveClass) {
                    continue;
                }
                this.newClasses.put(className, writer.toByteArray());
            }
        }
        this.verifier.setReferences(referencedClassMethods, referencedInterfaceMethods);
        return actualClassNames;
    }
    
    protected boolean loadClasses(final ClassLoader loader, final Map<String, URL> resolvedClasses) {
        for (final String className : resolvedClasses.keySet()) {
            try {
                loader.loadClass(Type.getObjectType(className).getClassName());
            }
            catch (ClassNotFoundException e) {
                this.logger.log(Level.FINER, "Error loading classes for {0} ({1}) : {2}", new Object[] { this.metaData.getImplementationTitle(), className, e.getMessage() });
                return false;
            }
        }
        return true;
    }
    
    private boolean isBootstrapClassName(final Collection<String> names) {
        final BootstrapLoader bootstrapLoader = BootstrapLoader.get();
        for (final String name : names) {
            if (bootstrapLoader.isBootstrapClass(name)) {
                return true;
            }
        }
        return false;
    }
    
    public Verifier getVerifier() {
        return this.verifier;
    }
    
    public IAgentLogger getLogger() {
        return this.logger;
    }
    
    public String getImplementationTitle() {
        return this.implementationTitle;
    }
    
    public float getImplementationVersion() {
        return this.implementationVersion;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    static ClassMatcher getClassMatcher(final MatchType type, final String className) {
        switch (type) {
            case Interface: {
                return new InterfaceMatcher(className);
            }
            case BaseClass: {
                return new ChildClassMatcher(className, false);
            }
            default: {
                return new ExactClassMatcher(className);
            }
        }
    }
    
    public void addClassMethodMatcher(final ClassAndMethodMatcher classAndMethodMatcher, final String className) {
        this.matcherBuilder.addClassMethodMatcher(classAndMethodMatcher);
    }
    
    public void addCloseable(final Closeable closeable) {
        this.closeables.add(closeable);
    }
    
    public Collection<Closeable> getCloseables() {
        return Collections.unmodifiableCollection((Collection<? extends Closeable>)this.closeables);
    }
    
    public ClassMatchVisitorFactory getMatcher() {
        return this.matcher;
    }
    
    public boolean matches(final String className) {
        return this.classNames.keySet().contains(className);
    }
    
    public boolean containsAbstractMatchers() {
        return !this.abstractMethods.isEmpty();
    }
    
    public boolean isWeaved(final String className) {
        return this.weaveClasses.containsKey(className);
    }
    
    public MixinClassVisitor getMixin(final String className) throws IOException {
        final WeavedClassInfo weavedClassInfo = this.weaveClasses.get(className);
        if (weavedClassInfo == null) {
            return null;
        }
        final byte[] bytes = this.classNames.get(className);
        if (bytes != null) {
            final ClassReader classReader = new ClassReader(bytes);
            final MixinClassVisitor cv = new MixinClassVisitor(bytes, this, weavedClassInfo);
            classReader.accept(cv, 8);
            if (this.metaData.isDebug()) {
                cv.print();
            }
            return cv;
        }
        return null;
    }
    
    public Map<String, byte[]> getClassBytes() {
        return this.classNames;
    }
    
    public boolean containsJDKClasses() {
        for (final String className : this.getClassBytes().keySet()) {
            if (className.startsWith("java/") || className.startsWith("sun/")) {
                return true;
            }
        }
        return false;
    }
    
    public Set<String> getClassNames() {
        final Set<String> names = (Set<String>)Sets.newHashSet();
        for (final String name : this.getClassBytes().keySet()) {
            names.add(Type.getObjectType(name).getClassName());
        }
        return names;
    }
    
    public ClassAppender getClassAppender() {
        return this.classAppender;
    }
    
    public String getClassMatch(final OptimizedClassMatcher.Match match) {
        for (final Collection<String> classNames : match.getClassMatches().values()) {
            for (final String className : classNames) {
                if (this.classNames.get(className) != null) {
                    return className;
                }
            }
        }
        return null;
    }
    
    public boolean isEnabled() {
        final Config config = ServiceFactory.getConfigService().getDefaultAgentConfig().getClassTransformerConfig().getInstrumentationConfig(this.implementationTitle);
        if (!config.getProperty("enabled", this.metaData.isEnabled())) {
            this.logger.log(Level.FINE, "Disabled instrumentation \"{0}\"", new Object[] { this.implementationTitle });
            return false;
        }
        return true;
    }
    
    public String toString() {
        return this.implementationTitle + " instrumentation";
    }
    
    public WeavedClassInfo getWeavedClassDetails(final String internalName) {
        return this.weaveClasses.get(internalName);
    }
    
    public Map<String, WeavedClassInfo> getWeaveClasses() {
        return this.weaveClasses;
    }
    
    public ClassWriter getClassWriter(final int flags, final ClassLoader loader) {
        final ClassResolver classResolver = ClassResolvers.getMultiResolver(this, ClassResolvers.getClassLoaderResolver(loader));
        return new PatchedClassWriter(2, classResolver);
    }
    
    public InputStream getClassResource(final String internalName) throws IOException {
        final byte[] bytes = this.newClasses.get(internalName);
        if (bytes != null) {
            return new ByteArrayInputStream(bytes);
        }
        return null;
    }
    
    public MixinClassVisitor getMixinClassVisitor(final String... matchClassNames) throws IOException {
        for (final String matchClassName : matchClassNames) {
            final MixinClassVisitor mixin = this.getMixin(matchClassName);
            if (mixin != null) {
                return mixin;
            }
        }
        return null;
    }
    
    Set<String> getSkipClasses() {
        return this.skipClasses;
    }
    
    private class GatherClassMethodMatchers extends ClassVisitor
    {
        private final List<Method> methods;
        private final String className;
        private final MatchType matchType;
        private final WeavedClassInfo instrumentationClass;
        
        public GatherClassMethodMatchers(final ClassVisitor cv, final String className, final WeavedClassInfo instrumentationClass) {
            super(327680, cv);
            this.methods = (List<Method>)Lists.newArrayList();
            this.className = className;
            this.matchType = instrumentationClass.getMatchType();
            this.instrumentationClass = instrumentationClass;
        }
        
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            final Method method = new Method(name, desc);
            final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (OptimizedClassMatcher.DEFAULT_CONSTRUCTOR.getName().equals(name) && !OptimizedClassMatcher.DEFAULT_CONSTRUCTOR.getDescriptor().equals(desc)) {
                this.methods.add(method);
                return mv;
            }
            return new MethodVisitor(327680, mv) {
                public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                    if (MergeMethodVisitor.isOriginalMethodInvocation(owner, name, desc)) {
                        GatherClassMethodMatchers.this.methods.add(method);
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        }
        
        public void visitEnd() {
            super.visitEnd();
            List<Method> methods = this.methods;
            methods.remove(OptimizedClassMatcher.DEFAULT_CONSTRUCTOR);
            if (methods.isEmpty()) {
                if (this.instrumentationClass.getTracedMethods().isEmpty()) {
                    InstrumentationPackage.this.logger.fine(this.className + " is marked as a weaved class, but no methods are matched to be weaved.");
                    return;
                }
                methods = (List<Method>)Lists.newArrayList((Iterable<?>)this.instrumentationClass.getTracedMethods().keySet());
            }
            final ClassMatcher classMatcher = InstrumentationPackage.getClassMatcher(this.matchType, this.className);
            for (final Method m : methods) {
                if (!this.matchType.isExactMatch()) {
                    InstrumentationPackage.this.abstractMethods.put(m, this.className);
                }
                InstrumentationPackage.this.addClassMethodMatcher(new DefaultClassAndMethodMatcher(classMatcher, new ExactMethodMatcher(m.getName(), m.getDescriptor())), this.className);
            }
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.util.asm.ClassResolvers;
import com.newrelic.agent.util.asm.BenignClassReadException;
import com.newrelic.agent.util.asm.Utils;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.CountDownLatch;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.Arrays;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import java.util.Collections;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.Multimaps;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.base.Supplier;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.util.asm.ClassResolver;
import java.util.List;
import java.security.ProtectionDomain;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.instrumentation.PointCut;
import java.util.Map;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;

public class InstrumentationContext implements TraceDetailsList
{
    private static final TraceInformation EMPTY_TRACE_INFO;
    protected final byte[] bytes;
    private boolean modified;
    private Multimap<Method, String> weavedMethods;
    protected boolean print;
    private Set<Method> timedMethods;
    private Map<Method, PointCut> oldReflectionStyleInstrumentationMethods;
    private Map<Method, PointCut> oldInvokerStyleInstrumentationMethods;
    private TraceInformation tracedInfo;
    private Map<ClassMatchVisitorFactory, OptimizedClassMatcher.Match> matches;
    private Map<Method, Method> bridgeMethods;
    private final Class<?> classBeingRedefined;
    private final ProtectionDomain protectionDomain;
    private List<ClassResolver> classResolvers;
    private boolean generated;
    private boolean hasSource;
    
    public InstrumentationContext(final byte[] bytes, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain) {
        this.bytes = bytes;
        this.classBeingRedefined = classBeingRedefined;
        this.protectionDomain = protectionDomain;
    }
    
    public Class<?> getClassBeingRedefined() {
        return this.classBeingRedefined;
    }
    
    public ProtectionDomain getProtectionDomain() {
        return this.protectionDomain;
    }
    
    public void markAsModified() {
        this.modified = true;
    }
    
    public boolean isModified() {
        return this.modified;
    }
    
    public TraceInformation getTraceInformation() {
        return (this.tracedInfo == null) ? InstrumentationContext.EMPTY_TRACE_INFO : this.tracedInfo;
    }
    
    public boolean isTracerMatch() {
        return this.tracedInfo != null && this.tracedInfo.isMatch();
    }
    
    public void addWeavedMethod(final Method method, final String instrumentationTitle) {
        if (this.weavedMethods == null) {
            this.weavedMethods = Multimaps.newSetMultimap(Maps.<Method, Collection<String>>newHashMap(), new Supplier<Set<String>>() {
                public Set<String> get() {
                    return Sets.newHashSet();
                }
            });
        }
        this.weavedMethods.put(method, instrumentationTitle);
        this.modified = true;
    }
    
    public void printBytecode() {
        this.print = true;
    }
    
    public PointCut getOldStylePointCut(final Method method) {
        PointCut pc = this.getOldInvokerStyleInstrumentationMethods().get(method);
        if (null == pc) {
            pc = this.getOldReflectionStyleInstrumentationMethods().get(method);
        }
        return pc;
    }
    
    private Map<Method, PointCut> getOldInvokerStyleInstrumentationMethods() {
        return (this.oldInvokerStyleInstrumentationMethods == null) ? Collections.<Method, PointCut>emptyMap() : this.oldInvokerStyleInstrumentationMethods;
    }
    
    private Map<Method, PointCut> getOldReflectionStyleInstrumentationMethods() {
        return (this.oldReflectionStyleInstrumentationMethods == null) ? Collections.<Method, PointCut>emptyMap() : this.oldReflectionStyleInstrumentationMethods;
    }
    
    public Set<Method> getWeavedMethods() {
        return (this.weavedMethods == null) ? Collections.<Method>emptySet() : this.weavedMethods.keySet();
    }
    
    public Set<Method> getTimedMethods() {
        return (this.timedMethods == null) ? Collections.<Method>emptySet() : this.timedMethods;
    }
    
    public Collection<String> getMergeInstrumentationPackages(final Method method) {
        return (Collection<String>)((this.weavedMethods == null) ? Collections.emptySet() : this.weavedMethods.asMap().get(method));
    }
    
    public boolean isModified(final Method method) {
        return this.getTimedMethods().contains(method) || this.getWeavedMethods().contains(method);
    }
    
    public void addTimedMethods(final Method... methods) {
        if (this.timedMethods == null) {
            this.timedMethods = Sets.newHashSet();
        }
        Collections.addAll(this.timedMethods, methods);
        this.modified = true;
    }
    
    public void addOldReflectionStyleInstrumentationMethod(final Method method, final PointCut pointCut) {
        if (this.oldReflectionStyleInstrumentationMethods == null) {
            this.oldReflectionStyleInstrumentationMethods = Maps.newHashMap();
        }
        this.oldReflectionStyleInstrumentationMethods.put(method, pointCut);
        this.modified = true;
    }
    
    public void addOldInvokerStyleInstrumentationMethod(final Method method, final PointCut pointCut) {
        if (this.oldInvokerStyleInstrumentationMethods == null) {
            this.oldInvokerStyleInstrumentationMethods = Maps.newHashMap();
        }
        this.oldInvokerStyleInstrumentationMethods.put(method, pointCut);
        this.modified = true;
    }
    
    public Map<ClassMatchVisitorFactory, OptimizedClassMatcher.Match> getMatches() {
        return (this.matches == null) ? Collections.<ClassMatchVisitorFactory, OptimizedClassMatcher.Match>emptyMap() : this.matches;
    }
    
    byte[] processTransformBytes(final byte[] originalBytes, final byte[] newBytes) {
        if (null != newBytes) {
            this.markAsModified();
            return newBytes;
        }
        return originalBytes;
    }
    
    public void putTraceAnnotation(final Method method, final TraceDetails traceDetails) {
        if (this.tracedInfo == null) {
            this.tracedInfo = new TraceInformation();
        }
        this.tracedInfo.putTraceAnnotation(method, traceDetails);
    }
    
    public void addIgnoreApdexMethod(final String methodName, final String methodDesc) {
        if (this.tracedInfo == null) {
            this.tracedInfo = new TraceInformation();
        }
        this.tracedInfo.addIgnoreApdexMethod(methodName, methodDesc);
    }
    
    public void addIgnoreTransactionMethod(final String methodName, final String methodDesc) {
        if (this.tracedInfo == null) {
            this.tracedInfo = new TraceInformation();
        }
        this.tracedInfo.addIgnoreTransactionMethod(methodName, methodDesc);
    }
    
    public void addIgnoreTransactionMethod(final Method m) {
        if (this.tracedInfo == null) {
            this.tracedInfo = new TraceInformation();
        }
        this.tracedInfo.addIgnoreTransactionMethod(m);
    }
    
    public void putMatch(final ClassMatchVisitorFactory matcher, final OptimizedClassMatcher.Match match) {
        if (this.matches == null) {
            this.matches = Maps.newHashMap();
        }
        this.matches.put(matcher, match);
    }
    
    public void addTracedMethods(final Map<Method, TraceDetails> tracedMethods) {
        if (this.tracedInfo == null) {
            this.tracedInfo = new TraceInformation();
        }
        this.tracedInfo.pullAll(tracedMethods);
    }
    
    public void addTrace(final Method method, final TraceDetails traceDetails) {
        if (this.tracedInfo == null) {
            this.tracedInfo = new TraceInformation();
        }
        this.tracedInfo.putTraceAnnotation(method, traceDetails);
    }
    
    public void match(final ClassLoader loader, final Class<?> classBeingRedefined, final ClassReader reader, final Collection<ClassMatchVisitorFactory> classVisitorFactories) {
        ClassVisitor visitor = null;
        for (final ClassMatchVisitorFactory factory : classVisitorFactories) {
            final ClassVisitor nextVisitor = factory.newClassMatchVisitor(loader, classBeingRedefined, reader, visitor, this);
            if (nextVisitor != null) {
                visitor = nextVisitor;
            }
        }
        if (visitor != null) {
            reader.accept(visitor, 1);
            if (this.bridgeMethods != null) {
                this.resolveBridgeMethods(reader);
            }
            else {
                this.bridgeMethods = ImmutableMap.of();
            }
        }
    }
    
    private void resolveBridgeMethods(final ClassReader reader) {
        final ClassVisitor visitor = new ClassVisitor(327680) {
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                final Method method = new Method(name, desc);
                if (InstrumentationContext.this.bridgeMethods.containsKey(method)) {
                    return new MethodVisitor(327680) {
                        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                            InstrumentationContext.this.bridgeMethods.put(method, new Method(name, desc));
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    };
                }
                return null;
            }
        };
        reader.accept(visitor, 6);
    }
    
    public static Set<Class<?>> getMatchingClasses(final Collection<ClassMatchVisitorFactory> matchers, final Class<?>... classes) {
        final Set<Class<?>> matchingClasses = Sets.newConcurrentHashSet();
        if (classes == null || classes.length == 0) {
            return matchingClasses;
        }
        final double partitions = (classes.length < 8) ? classes.length : 8.0;
        final int estimatedPerPartition = (int)Math.ceil(classes.length / partitions);
        final List<List<Class<?>>> partitionsClasses = Lists.partition(Arrays.asList(classes), estimatedPerPartition);
        final CountDownLatch countDownLatch = new CountDownLatch(partitionsClasses.size());
        for (final List<Class<?>> partitionClasses : partitionsClasses) {
            final Runnable matchingRunnable = new Runnable() {
                public void run() {
                    for (final Class<?> clazz : partitionClasses) {
                        if (isMatch(matchers, clazz)) {
                            matchingClasses.add(clazz);
                        }
                    }
                    countDownLatch.countDown();
                }
            };
            new Thread(matchingRunnable).start();
        }
        try {
            countDownLatch.await();
        }
        catch (InterruptedException e) {
            Agent.LOG.log(Level.INFO, "Failed to wait for matching classes");
            Agent.LOG.log(Level.FINER, e, "Interrupted during class matching");
        }
        return matchingClasses;
    }
    
    private static boolean isMatch(final Collection<ClassMatchVisitorFactory> matchers, final Class<?> clazz) {
        if (clazz.isArray()) {
            return false;
        }
        if (clazz.getName().startsWith("com.newrelic.api.agent") || clazz.getName().startsWith("com.newrelic.agent.bridge")) {
            return false;
        }
        ClassLoader loader = clazz.getClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        final InstrumentationContext context = new InstrumentationContext(null, null, null);
        try {
            final ClassReader reader = Utils.readClass(clazz);
            context.match(loader, clazz, reader, matchers);
            return !context.getMatches().isEmpty();
        }
        catch (BenignClassReadException ex2) {
            return false;
        }
        catch (Exception ex) {
            if (clazz.getName().startsWith("com.newrelic") || clazz.getName().startsWith("weave.")) {
                return false;
            }
            Agent.LOG.log(Level.FINER, "Unable to read {0}", clazz.getName());
            Agent.LOG.log(Level.FINEST, (Throwable)ex, "Unable to read {0}", clazz.getName());
            return false;
        }
    }
    
    public void addBridgeMethod(final Method method) {
        if (this.bridgeMethods == null) {
            this.bridgeMethods = Maps.newHashMap();
        }
        this.bridgeMethods.put(method, method);
    }
    
    public Map<Method, Method> getBridgeMethods() {
        return this.bridgeMethods;
    }
    
    public boolean isUsingLegacyInstrumentation() {
        return null != this.oldInvokerStyleInstrumentationMethods || null != this.oldReflectionStyleInstrumentationMethods;
    }
    
    public boolean hasModifiedClassStructure() {
        return null != this.oldInvokerStyleInstrumentationMethods;
    }
    
    public void addClassResolver(final ClassResolver classResolver) {
        if (this.classResolvers == null) {
            this.classResolvers = Lists.newArrayList();
        }
        this.classResolvers.add(classResolver);
    }
    
    public ClassResolver getClassResolver(final ClassLoader loader) {
        ClassResolver classResolver = ClassResolvers.getClassLoaderResolver(loader);
        if (this.classResolvers != null) {
            this.classResolvers.add(classResolver);
            classResolver = ClassResolvers.getMultiResolver(this.classResolvers);
        }
        return classResolver;
    }
    
    public byte[] getOriginalClassBytes() {
        return this.bytes;
    }
    
    public void setGenerated(final boolean isGenerated) {
        this.generated = isGenerated;
    }
    
    public boolean isGenerated() {
        return this.generated;
    }
    
    public void setSourceAttribute(final boolean hasSource) {
        this.hasSource = hasSource;
    }
    
    public boolean hasSourceAttribute() {
        return this.hasSource;
    }
    
    static {
        EMPTY_TRACE_INFO = new TraceInformation();
    }
}

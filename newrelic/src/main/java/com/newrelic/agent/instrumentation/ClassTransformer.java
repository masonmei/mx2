// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.lang.instrument.UnmodifiableClassException;
import com.newrelic.agent.util.Invoker;
import java.lang.instrument.ClassDefinition;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.lang.reflect.InvocationHandler;
import com.newrelic.agent.TracerService;
import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.text.MessageFormat;
import java.lang.annotation.Annotation;
import com.newrelic.agent.util.Annotations;
import com.newrelic.agent.bridge.AgentBridge;
import java.util.Iterator;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import java.util.Collections;
import com.newrelic.agent.errors.ErrorService;
import java.util.LinkedList;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.InstrumentationProxy;
import java.util.Collection;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;

public class ClassTransformer implements ContextClassTransformer
{
    protected final Collection<PointCut> pointcuts;
    private final int classreaderFlags;
    private final InstrumentationProxy instrumentation;
    private final boolean retransformSupported;
    protected final ClassNameFilter classNameFilter;
    private final IAgentLogger logger;
    private final ClassMatchVisitorFactory matcher;
    
    protected ClassTransformer(final InstrumentationProxy pInstrumentation, final boolean pRetransformSupported) {
        this.instrumentation = pInstrumentation;
        this.logger = Agent.LOG.getChildLogger(ClassTransformer.class);
        this.initAgentHandle();
        this.classNameFilter = new ClassNameFilter(this.logger);
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.classNameFilter.addConfigClassFilters(config);
        this.classNameFilter.addExcludeFileClassFilters();
        this.classreaderFlags = this.instrumentation.getClassReaderFlags();
        this.retransformSupported = pRetransformSupported;
        final List<PointCut> pcs = new LinkedList<PointCut>(this.findEnabledPointCuts());
        pcs.addAll(ErrorService.getEnabledErrorHandlerPointCuts());
        Collections.sort(pcs);
        this.pointcuts = Collections.unmodifiableCollection((Collection<? extends PointCut>)pcs);
        this.setPointcutProperties();
        this.matcher = OptimizedClassMatcherBuilder.newBuilder().addClassMethodMatcher((ClassAndMethodMatcher[])this.pointcuts.toArray(new PointCut[0])).build();
    }
    
    public ClassMatchVisitorFactory getMatcher() {
        return this.matcher;
    }
    
    private void setPointcutProperties() {
        final List<PointCutInvocationHandler> handlers = new ArrayList<PointCutInvocationHandler>(this.pointcuts.size());
        final Collection<ClassMatcher> classMatchers = new ArrayList<ClassMatcher>();
        for (final PointCut pc : this.pointcuts) {
            handlers.add(pc.getPointCutInvocationHandler());
            classMatchers.add(pc.getClassMatcher());
        }
        this.classNameFilter.addClassMatcherIncludes(classMatchers);
        ServiceFactory.getTracerService().registerInvocationHandlers(handlers);
        this.logger.finer("A Class transformer is initialized");
    }
    
    private void initAgentHandle() {
        AgentBridge.agentHandler = AgentWrapper.getAgentWrapper(this);
    }
    
    Collection<PointCut> findEnabledPointCuts() {
        final Collection<Class<?>> classes = Annotations.getAnnotationClassesFromManifest(com.newrelic.agent.instrumentation.pointcuts.PointCut.class, "com/newrelic/agent/instrumentation/pointcuts");
        final Collection<PointCut> pointcuts = new ArrayList<PointCut>();
        for (final Class clazz : classes) {
            final PointCut pc = this.createPointCut(clazz);
            if (pc.isEnabled()) {
                pointcuts.add(pc);
            }
        }
        return pointcuts;
    }
    
    private PointCut createPointCut(final Class<PointCut> clazz) {
        try {
            return clazz.getConstructor(ClassTransformer.class).newInstance(this);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unable to create pointcut {0} : {1}", clazz.getName(), e.toString());
            Agent.LOG.severe(msg);
            Agent.LOG.log(Level.FINE, msg, e);
            return null;
        }
    }
    
    public Collection<PointCut> getPointcuts() {
        return this.pointcuts;
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
        if (!this.shouldTransform(loader, className, classfileBuffer)) {
            this.logger.trace(MessageFormat.format("Skipped instrumenting {0}", className));
            return null;
        }
        try {
            final WeavingLoaderImpl weavingLoader = this.getWeavingLoader(loader);
            if (Agent.isDebugEnabled() && this.logger.isTraceEnabled()) {
                this.logger.trace(MessageFormat.format("Considering instrumenting {0}", className));
            }
            return weavingLoader.preProcess(context, className, classBeingRedefined, classfileBuffer, match);
        }
        catch (ThreadDeath e) {
            throw e;
        }
        catch (Throwable e2) {
            this.logger.severe(MessageFormat.format("An error occurred processing class {0} : {1}", className, e2.toString()));
            if (Agent.isDebugEnabled()) {
                e2.printStackTrace();
            }
            return null;
        }
    }
    
    protected boolean shouldTransform(final ClassLoader loader, final String className, final byte[] classfileBuffer) {
        final boolean isLoggable = Agent.isDebugEnabled() && this.logger.isLoggable(Level.FINEST);
        if (this.isIncluded(className)) {
            if (isLoggable) {
                this.logger.finest(MessageFormat.format("Class {0} is explicitly included", className));
            }
            return true;
        }
        if (this.isExcluded(className)) {
            if (isLoggable) {
                this.logger.finest(MessageFormat.format("Skipping class {0} because it is excluded", className));
            }
            return false;
        }
        if (className.startsWith("$")) {
            if (isLoggable) {
                this.logger.finest(MessageFormat.format("Skipping class {0} because it starts with $", className));
            }
            return false;
        }
        if (className.indexOf("$$") > 0 && !className.startsWith("play")) {
            if (isLoggable) {
                this.logger.finest(MessageFormat.format("Skipping class {0} because it contains $$ and is not a Play class", className));
            }
            return false;
        }
        if (this.isValidClassByteArray(classfileBuffer)) {
            if (isLoggable) {
                this.logger.finest(MessageFormat.format("Skipping class {0} because it does not appear to be a valid class file", className));
            }
            return false;
        }
        if (loader == null && !this.isBootstrapClassInstrumentationEnabled()) {
            if (isLoggable) {
                this.logger.finest(MessageFormat.format("Skipping class {0} because bootstrap class instrumentation is not supported", className));
            }
            return false;
        }
        return true;
    }
    
    private boolean isBootstrapClassInstrumentationEnabled() {
        return this.instrumentation.isBootstrapClassInstrumentationEnabled();
    }
    
    protected boolean isIncluded(final String className) {
        return this.classNameFilter.isIncluded(className);
    }
    
    protected boolean isExcluded(final String className) {
        return this.classNameFilter.isExcluded(className);
    }
    
    protected boolean isRetransformSupported() {
        return this.retransformSupported;
    }
    
    private boolean isValidClassByteArray(final byte[] classfileBuffer) {
        return classfileBuffer.length >= 4 && classfileBuffer[0] == -54 && classfileBuffer[0] == -2 && classfileBuffer[0] == -70 && classfileBuffer[0] == -66;
    }
    
    protected int getClassReaderFlags() {
        return this.classreaderFlags;
    }
    
    protected WeavingLoaderImpl getWeavingLoader(final ClassLoader loader) {
        return new WeavingLoaderImpl(loader);
    }
    
    protected WeavingLoaderImpl getWeavingLoader(final ClassLoader loader, final boolean pIsRetrans) {
        return new WeavingLoaderImpl(loader);
    }
    
    InstrumentationProxy getInstrumentation() {
        return this.instrumentation;
    }
    
    public final ClassNameFilter getClassNameFilter() {
        return this.classNameFilter;
    }
    
    public InvocationHandler evaluate(final Class clazz, final TracerService tracerService, final Object className, final Object methodName, final Object methodDesc, final boolean ignoreApdex, final Object[] args) {
        final ClassMethodSignature classMethodSignature = new ClassMethodSignature(((String)className).replace('/', '.'), (String)methodName, (String)methodDesc);
        for (final PointCut pc : this.getPointcuts()) {
            if (pc.getClassMatcher().isMatch(clazz) && pc.getMethodMatcher().matches(-1, classMethodSignature.getMethodName(), classMethodSignature.getMethodDesc(), MethodMatcher.UNSPECIFIED_ANNOTATIONS)) {
                final PointCutInvocationHandler invocationHandler = pc.getPointCutInvocationHandler();
                return InvocationPoint.getInvocationPoint(invocationHandler, tracerService, classMethodSignature, ignoreApdex);
            }
        }
        if (ignoreApdex) {
            return IgnoreApdexInvocationHandler.INVOCATION_HANDLER;
        }
        Agent.LOG.log(Level.FINE, "No invocation handler was registered for {0}", new Object[] { classMethodSignature });
        return NoOpInvocationHandler.INVOCATION_HANDLER;
    }
    
    public static boolean isInstrumented(final Class<?> clazz) {
        return clazz.getAnnotation(InstrumentedClass.class) != null;
    }
    
    public static boolean isInstrumentedAndModified(final Class<?> clazz) {
        return clazz.getAnnotation(InstrumentedClass.class) != null && clazz.getAnnotation(InstrumentedClass.class).classStructureModified();
    }
    
    public static boolean canModifyClassStructure(final ClassLoader classLoader, final Class<?> classBeingRedefined) {
        return !hasBeenLoaded(classBeingRedefined) || isInstrumentedAndModified(classBeingRedefined);
    }
    
    public static boolean hasBeenLoaded(final Class<?> clazz) {
        return null != clazz;
    }
    
    class WeavingLoaderImpl
    {
        private final ClassLoader classLoader;
        
        public WeavingLoaderImpl(final ClassLoader classLoader) {
            this.classLoader = classLoader;
        }
        
        public byte[] preProcess(final InstrumentationContext context, final String className, final Class<?> classBeingRedefined, final byte[] classfileBuffer, final OptimizedClassMatcher.Match match) {
            ClassReader cr = new ClassReader(classfileBuffer);
            if (InstrumentationUtils.isInterface(cr)) {
                return null;
            }
            final Collection<PointCut> strongMatches = (Collection<PointCut>)Lists.newArrayList((Iterable<?>)ClassTransformer.this.pointcuts);
            strongMatches.retainAll(match.getClassMatches().keySet());
            if (strongMatches.isEmpty()) {
                return null;
            }
            if (this.classLoader != null && !InstrumentationUtils.isAbleToResolveAgent(this.classLoader, className)) {
                final String msg = MessageFormat.format("Not instrumenting {0}: class loader unable to load agent classes", className);
                Agent.LOG.log(Level.FINER, msg);
                return null;
            }
            try {
                if (ClassTransformer.canModifyClassStructure(this.classLoader, classBeingRedefined)) {
                    final byte[] classfileBufferWithUID = InstrumentationUtils.generateClassBytesWithSerialVersionUID(cr, ClassTransformer.this.classreaderFlags, this.classLoader);
                    cr = new ClassReader(classfileBufferWithUID);
                }
                final ClassWriter cw = InstrumentationUtils.getClassWriter(cr, this.classLoader);
                final GenericClassAdapter adapter = new GenericClassAdapter(cw, this.classLoader, className, classBeingRedefined, strongMatches, context);
                cr.accept(adapter, ClassTransformer.this.classreaderFlags);
                if (adapter.getInstrumentedMethods().size() > 0) {
                    if (Agent.LOG.isFinerEnabled()) {
                        final String msg2 = MessageFormat.format("Instrumenting {0}", className);
                        Agent.LOG.finer(msg2);
                    }
                    return cw.toByteArray();
                }
                return null;
            }
            catch (StopProcessingException e2) {
                return null;
            }
            catch (ArrayIndexOutOfBoundsException t) {
                String msg3 = MessageFormat.format("Skipping transformation of class {0} ({1} bytes) because an ASM array bounds exception occurred: {2}", className, classfileBuffer.length, t.toString());
                ClassTransformer.this.logger.warning(msg3);
                if (ClassTransformer.this.logger.isLoggable(Level.FINER)) {
                    msg3 = MessageFormat.format("ASM error for pointcut(s) : strong {0}", strongMatches);
                    ClassTransformer.this.logger.finer(msg3);
                    ClassTransformer.this.logger.log(Level.FINER, "ASM error", t);
                }
                if (Boolean.getBoolean("newrelic.asm.error.stop")) {
                    System.exit(-1);
                }
                return null;
            }
            catch (ThreadDeath e) {
                throw e;
            }
            catch (Throwable t2) {
                ClassTransformer.this.logger.warning(MessageFormat.format("Skipping transformation of class {0} because an error occurred: {1}", className, t2.toString()));
                if (ClassTransformer.this.logger.isLoggable(Level.FINER)) {
                    ClassTransformer.this.logger.log(Level.FINER, "Error transforming class " + className, t2);
                }
                return null;
            }
        }
        
        private boolean isAbleToResolveAgent(final ClassLoader loader) {
            try {
                ClassLoaderCheck.loadAgentClass(loader);
                return true;
            }
            catch (Throwable t) {
                final String msg = MessageFormat.format("Classloader {0} failed to load Agent class. The agent might need to be loaded by the bootstrap classloader.: {1}", loader.getClass().getName(), t);
                if (Agent.LOG.isLoggable(Level.FINEST)) {
                    Agent.LOG.log(Level.FINEST, msg, t);
                }
                else if (Agent.LOG.isLoggable(Level.FINER)) {
                    Agent.LOG.finer(msg);
                }
                return false;
            }
        }
        
        private synchronized void redefineClass(final String className, final byte[] classfileBuffer) {
            try {
                ClassTransformer.this.instrumentation.redefineClasses(new ClassDefinition(this.classLoader.loadClass(Invoker.getClassNameFromInternalName(className)), classfileBuffer));
            }
            catch (ClassNotFoundException e) {
                final String msg = MessageFormat.format("An error occurred redefining class {0}: {1}", className, e);
                if (ClassTransformer.this.logger.isLoggable(Level.FINEST)) {
                    ClassTransformer.this.logger.log(Level.FINEST, msg, e);
                }
                else if (ClassTransformer.this.logger.isLoggable(Level.FINER)) {
                    ClassTransformer.this.logger.finer(msg);
                }
            }
            catch (UnmodifiableClassException e2) {
                final String msg = MessageFormat.format("An error occurred redefining class {0}: {1}", className, e2);
                if (ClassTransformer.this.logger.isLoggable(Level.FINEST)) {
                    ClassTransformer.this.logger.log(Level.FINEST, msg, e2);
                }
                else if (ClassTransformer.this.logger.isLoggable(Level.FINER)) {
                    ClassTransformer.this.logger.finer(msg);
                }
            }
        }
        
        public ClassLoader getClassLoader() {
            return this.classLoader;
        }
    }
}

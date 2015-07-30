// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import java.security.ProtectionDomain;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import java.lang.instrument.UnmodifiableClassException;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import java.util.Collection;
import java.util.Iterator;
import com.newrelic.agent.instrumentation.pointcuts.database.ConnectionClassTransformer;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.context.ContextClassTransformer;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.config.AgentConfig;
import java.util.concurrent.ThreadFactory;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.Executors;
import com.newrelic.agent.util.DefaultThreadFactory;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.Logger;
import java.util.Collections;
import java.util.ArrayList;
import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.instrumentation.context.ClassMatchVisitorFactory;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.ScheduledExecutorService;
import com.newrelic.agent.instrumentation.context.InstrumentationContextManager;
import java.lang.instrument.ClassFileTransformer;
import java.util.List;
import com.newrelic.agent.instrumentation.custom.ClassRetransformer;
import com.newrelic.agent.service.AbstractService;

public class ClassTransformerServiceImpl extends AbstractService implements ClassTransformerService
{
    private final boolean isEnabled;
    private volatile ClassTransformer classTransformer;
    private volatile ClassRetransformer localRetransformer;
    private volatile ClassRetransformer remoteRetransformer;
    private final List<ClassFileTransformer> classTransformers;
    private final long shutdownTime;
    private InstrumentationContextManager contextManager;
    private TraceMatchTransformer traceMatchTransformer;
    private final InstrumentationImpl instrumentation;
    private final ScheduledExecutorService executor;
    private final Instrumentation extensionInstrumentation;
    private final AtomicReference<Set<ClassMatchVisitorFactory>> retransformClassMatchers;
    
    public ClassTransformerServiceImpl(final InstrumentationProxy instrumentationProxy) throws Exception {
        super(ClassTransformerServiceImpl.class.getSimpleName());
        this.classTransformers = Collections.synchronizedList(new ArrayList<ClassFileTransformer>());
        this.retransformClassMatchers = new AtomicReference<Set<ClassMatchVisitorFactory>>(this.createRetransformClassMatcherList());
        this.extensionInstrumentation = new ExtensionInstrumentation(instrumentationProxy);
        this.instrumentation = new InstrumentationImpl((Logger)this.logger);
        AgentBridge.instrumentation = (com.newrelic.agent.bridge.Instrumentation)this.instrumentation;
        final ThreadFactory factory = new DefaultThreadFactory("New Relic Retransformer", true);
        this.executor = Executors.newSingleThreadScheduledExecutor(factory);
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.isEnabled = config.getClassTransformerConfig().isEnabled();
        final long shutdownDelayInNanos = config.getClassTransformerConfig().getShutdownDelayInNanos();
        if (shutdownDelayInNanos > 0L) {
            this.shutdownTime = System.nanoTime() + shutdownDelayInNanos;
            final String msg = MessageFormat.format("The Class Transformer Service will stop instrumenting classes after {0} secs", TimeUnit.SECONDS.convert(shutdownDelayInNanos, TimeUnit.NANOSECONDS));
            this.getLogger().info(msg);
        }
        else {
            this.shutdownTime = Long.MAX_VALUE;
        }
    }
    
    private Set<ClassMatchVisitorFactory> createRetransformClassMatcherList() {
        return Sets.newSetFromMap((Map<ClassMatchVisitorFactory, Boolean>)Maps.newConcurrentMap());
    }
    
    protected void doStart() throws Exception {
        if (!this.isEnabled()) {
            this.getLogger().info("The class transformer is disabled.  No classes will be instrumented.");
            return;
        }
        final InstrumentationProxy instrProxy = ServiceFactory.getAgent().getInstrumentation();
        if (instrProxy == null) {
            this.getLogger().severe("Unable to initialize the class transformer because there is no instrumentation hook");
        }
        else {
            this.classTransformer = this.startClassTransformer(instrProxy);
        }
        this.executor.schedule(new Runnable() {
            public void run() {
                ClassTransformerServiceImpl.this.retransformMatchingClasses();
            }
        }, this.getRetransformPeriodInSeconds(), TimeUnit.SECONDS);
    }
    
    private long getRetransformPeriodInSeconds() {
        return (long)ServiceFactory.getConfigService().getDefaultAgentConfig().getValue("class_transformer.retransformation_period", (Object)10L);
    }
    
    public void checkShutdown() {
        if (this.shutdownTime == Long.MAX_VALUE || this.isStopped()) {
            return;
        }
        final long nsTilShutdown = this.shutdownTime - System.nanoTime();
        if (nsTilShutdown < 0L) {
            try {
                this.getLogger().info("Stopping Class Transformer Service based on configured shutdown_delay");
                this.stop();
            }
            catch (Exception e) {
                final String msg = MessageFormat.format("Failed to stop Class Transformer Service: {0}", e);
                this.getLogger().error(msg);
            }
        }
    }
    
    private ClassTransformer startClassTransformer(final InstrumentationProxy instrProxy) throws Exception {
        final boolean retransformSupported = this.isRetransformationSupported(instrProxy);
        final ClassTransformer classTransformer = new ClassTransformer(instrProxy, retransformSupported);
        (this.contextManager = InstrumentationContextManager.create(instrProxy, AgentBridge.class.getClassLoader() == null)).addContextClassTransformer(classTransformer.getMatcher(), classTransformer);
        for (final PointCut pc : classTransformer.getPointcuts()) {
            Agent.LOG.log(Level.FINEST, "pointcut {0} active", new Object[] { pc });
            pc.noticeTransformerStarted(classTransformer);
        }
        (this.localRetransformer = new ClassRetransformer(this.contextManager)).setClassMethodMatchers(ServiceFactory.getExtensionService().getEnabledPointCuts());
        this.remoteRetransformer = new ClassRetransformer(this.contextManager);
        this.traceMatchTransformer = new TraceMatchTransformer(this.contextManager);
        StartableClassFileTransformer[] arr$;
        final StartableClassFileTransformer[] startableClassTransformers = arr$ = new StartableClassFileTransformer[] { new InterfaceMixinClassTransformer(classTransformer.getClassReaderFlags()), new JDBCClassTransformer(classTransformer), new ConnectionClassTransformer(classTransformer) };
        for (final StartableClassFileTransformer transformer : arr$) {
            transformer.start(instrProxy, retransformSupported);
            this.classTransformers.add(transformer);
        }
        arr$ = InterfaceImplementationClassTransformer.getClassTransformers(classTransformer);
        for (final StartableClassFileTransformer transformer : arr$) {
            transformer.start(instrProxy, retransformSupported);
            this.classTransformers.add(transformer);
        }
        return classTransformer;
    }
    
    private boolean isRetransformationSupported(final InstrumentationProxy instrProxy) {
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        final Boolean enableClassRetransformation = config.getProperty("enable_class_retransformation");
        if (enableClassRetransformation != null) {
            return enableClassRetransformation;
        }
        try {
            return instrProxy.isRetransformClassesSupported();
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unexpected error asking current JVM configuration if it supports retransformation of classes: {0}", e);
            this.getLogger().warning(msg);
            return false;
        }
    }
    
    private void retransformMatchingClasses() {
        final Set<ClassMatchVisitorFactory> matchers = this.retransformClassMatchers.getAndSet(this.createRetransformClassMatcherList());
        if (!matchers.isEmpty()) {
            this.retransformMatchingClassesImmediately(matchers);
        }
    }
    
    public void retransformMatchingClasses(final Collection<ClassMatchVisitorFactory> matchers) {
        this.retransformClassMatchers.get().addAll(matchers);
    }
    
    public void retransformMatchingClassesImmediately(final Collection<ClassMatchVisitorFactory> matchers) {
        final InstrumentationProxy instrumentation = ServiceFactory.getAgent().getInstrumentation();
        final Set<Class<?>> classesToRetransform = InstrumentationContext.getMatchingClasses(matchers, (Class<?>[])instrumentation.getAllLoadedClasses());
        if (!classesToRetransform.isEmpty()) {
            try {
                instrumentation.retransformClasses((Class<?>[])classesToRetransform.toArray(new Class[0]));
            }
            catch (UnmodifiableClassException e) {
                this.logger.log(Level.FINER, "Error retransforming classes: " + classesToRetransform, e);
            }
        }
    }
    
    protected void doStop() throws Exception {
        this.executor.shutdown();
        final InstrumentationProxy instrProxy = ServiceFactory.getAgent().getInstrumentation();
        if (instrProxy == null) {
            return;
        }
        for (final ClassFileTransformer classFileTransformer : this.classTransformers) {
            instrProxy.removeTransformer(classFileTransformer);
        }
    }
    
    public InstrumentationContextManager getContextManager() {
        return this.contextManager;
    }
    
    public ClassTransformer getClassTransformer() {
        return this.classTransformer;
    }
    
    public ClassRetransformer getLocalRetransformer() {
        return this.localRetransformer;
    }
    
    public ClassRetransformer getRemoteRetransformer() {
        return this.remoteRetransformer;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public boolean addTraceMatcher(final ClassAndMethodMatcher matcher, final String metricPrefix) {
        return this.traceMatchTransformer.addTraceMatcher(matcher, metricPrefix);
    }
    
    public Instrumentation getExtensionInstrumentation() {
        return this.extensionInstrumentation;
    }
    
    private static class TraceMatchTransformer implements ContextClassTransformer
    {
        private final Map<ClassAndMethodMatcher, String> matchersPrefix;
        private final Set<ClassMatchVisitorFactory> matchVisitors;
        private final InstrumentationContextManager contextManager;
        
        TraceMatchTransformer(final InstrumentationContextManager manager) {
            this.matchersPrefix = (Map<ClassAndMethodMatcher, String>)Maps.newConcurrentMap();
            this.matchVisitors = Sets.newConcurrentHashSet();
            this.contextManager = manager;
        }
        
        public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
            for (final Method method : match.getMethods()) {
                for (final ClassAndMethodMatcher matcher : match.getClassMatches().keySet()) {
                    if (matcher.getMethodMatcher().matches(-1, method.getName(), method.getDescriptor(), match.getMethodAnnotations(method))) {
                        context.putTraceAnnotation(method, TraceDetailsBuilder.newBuilder().setMetricPrefix(this.matchersPrefix.get(matcher)).build());
                    }
                }
            }
            return null;
        }
        
        public boolean addTraceMatcher(final ClassAndMethodMatcher matcher, final String metricPrefix) {
            return !this.matchersPrefix.containsKey(matcher) && this.addMatchVisitor(matcher, metricPrefix);
        }
        
        private synchronized boolean addMatchVisitor(final ClassAndMethodMatcher matcher, final String metricPrefix) {
            if (!this.matchersPrefix.containsKey(matcher)) {
                this.matchersPrefix.put(matcher, metricPrefix);
                final OptimizedClassMatcherBuilder builder = OptimizedClassMatcherBuilder.newBuilder();
                builder.addClassMethodMatcher(matcher);
                final ClassMatchVisitorFactory matchVisitor = builder.build();
                this.matchVisitors.add(matchVisitor);
                this.contextManager.addContextClassTransformer(matchVisitor, this);
                return true;
            }
            return false;
        }
    }
}

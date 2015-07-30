// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.Agent;
import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.Config;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.*;
import com.newrelic.agent.deps.org.objectweb.asm.commons.JSRInlinerAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.InstrumentedClass;
import com.newrelic.agent.instrumentation.InstrumentedMethod;
import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.instrumentation.api.ApiImplementationUpdate;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.instrumentation.ejb3.EJBAnnotationVisitor;
import com.newrelic.agent.instrumentation.spring.SpringAnnotationVisitor;
import com.newrelic.agent.instrumentation.tracing.TraceClassTransformer;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.instrumentation.weaver.ClassWeaverService;
import com.newrelic.agent.instrumentation.weaver.NewClassMarker;
import com.newrelic.agent.instrumentation.weaver.WeavedMethod;
import com.newrelic.agent.instrumentation.webservices.RestAnnotationVisitor;
import com.newrelic.agent.instrumentation.webservices.WebServiceVisitor;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.servlet.ServletAnnotationVisitor;
import com.newrelic.agent.stats.StatsService;
import com.newrelic.agent.stats.StatsWorks;
import com.newrelic.agent.util.asm.PatchedClassWriter;
import com.newrelic.agent.util.asm.Utils;

import java.io.File;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class InstrumentationContextManager {
    private static final Set<String> MARKER_INTERFACES_TO_SKIP;
    private static final ContextClassTransformer NO_OP_TRANSFORMER;
    private final Map<ClassMatchVisitorFactory, ContextClassTransformer> matchVisitors;
    private final Map<ClassMatchVisitorFactory, ContextClassTransformer> interfaceMatchVisitors;
    private final Instrumentation instrumentation;
    private ClassChecker classChecker;
    private final ClassWeaverService classWeaverService;
    ClassFileTransformer transformer;
    private static final Set<String> ANNOTATIONS_TO_REMOVE;
    private final ContextClassTransformer FinishClassTransformer;

    public InstrumentationContextManager(final Instrumentation instrumentation) {
        this.matchVisitors = Maps.newConcurrentMap();
        this.interfaceMatchVisitors = Maps.newConcurrentMap();
        this.FinishClassTransformer = new ContextClassTransformer() {
            public byte[] transform(final ClassLoader loader, final String className,
                                    final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain,
                                    final byte[] classfileBuffer, final InstrumentationContext context,
                                    final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
                try {
                    return this.getFinalTransformation(loader, className, classBeingRedefined, classfileBuffer, context);
                } catch (Throwable ex) {
                    Agent.LOG.log(Level.FINE, "Unable to transform " + className, ex);
                    return null;
                }
            }

            private byte[] getFinalTransformation(final ClassLoader loader, final String className,
                                                  final Class<?> classBeingRedefined, final byte[] classfileBuffer,
                                                  final InstrumentationContext context) {
                final ClassReader reader = new ClassReader(classfileBuffer);
                ClassVisitor cv;
                final ClassWriter writer = (ClassWriter) (cv
                        = new PatchedClassWriter(2, context.getClassResolver(loader)));
                if (!context.getWeavedMethods().isEmpty()) {
                    cv = new MarkWeaverMethodsVisitor(cv, context);
                }
                cv = InstrumentationContextManager.this.addModifiedClassAnnotation(cv, context);
                cv = InstrumentationContextManager.this.addModifiedMethodAnnotation(cv, context, loader);
                cv = new ClassVisitor(327680, cv) {
                    public void visit(int version, final int access, final String name, final String signature,
                                      final String superName, final String[] interfaces) {
                        if (version < 49 || version > 100) {
                            Agent.LOG.log(Level.FINEST, "Converting {0} from version {1} to {2}", new Object[]{
                                    name, version, 49
                            });
                            version = 49;
                        }
                        super.visit(version, access, name, signature, superName, interfaces);
                    }

                    public MethodVisitor visitMethod(final int access, final String name, final String desc,
                                                     final String signature, final String[] exceptions) {
                        return new JSRInlinerAdapter(super.visitMethod(access, name, desc, signature, exceptions), access, name, desc, signature, exceptions);
                    }
                };
                cv = this.skipExistingAnnotations(cv);
                cv = CurrentTransactionRewriter.rewriteCurrentTransactionReferences(cv, reader);
                reader.accept(cv, 4);
                if (InstrumentationContextManager.this.classChecker != null) {
                    InstrumentationContextManager.this.classChecker.check(writer.toByteArray());
                }
                if (Agent.isDebugEnabled()) {
                    try {
                        final File old = File.createTempFile(className.replace('/', '_'), ".old");
                        Utils.print(context.bytes, new PrintWriter(old));
                        Agent.LOG.debug("Wrote " + old.getAbsolutePath());
                        final File newFile = File.createTempFile(className.replace('/', '_'), ".new");
                        Utils.print(writer.toByteArray(), new PrintWriter(newFile));
                        Agent.LOG.debug("Wrote " + newFile.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                this.addSupportabilityMetrics(reader, className, context);
                Agent.LOG.finer("Final transformation of class " + className);
                return writer.toByteArray();
            }

            private ClassVisitor skipExistingAnnotations(final ClassVisitor cv) {
                return new ClassVisitor(327680, cv) {
                    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                        if (InstrumentationContextManager.ANNOTATIONS_TO_REMOVE.contains(desc)) {
                            return null;
                        }
                        return super.visitAnnotation(desc, visible);
                    }

                    public MethodVisitor visitMethod(final int access, final String name, final String desc,
                                                     final String signature, final String[] exceptions) {
                        return new MethodVisitor(327680, super.visitMethod(access, name, desc, signature, exceptions)) {
                            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                                if (InstrumentationContextManager.ANNOTATIONS_TO_REMOVE.contains(desc)) {
                                    return null;
                                }
                                return super.visitAnnotation(desc, visible);
                            }
                        };
                    }
                };
            }

            private void addSupportabilityMetrics(final ClassReader reader, final String className,
                                                  final InstrumentationContext context) {
                final StatsService statsService = ServiceFactory.getStatsService();
                if (statsService != null) {
                    for (final Method m : context.getTimedMethods()) {
                        final TraceDetails traceDetails = context.getTraceInformation().getTraceAnnotations().get(m);
                        if (traceDetails != null && traceDetails.isCustom()) {
                            statsService.doStatsWork(StatsWorks.getRecordMetricWork(MessageFormat.format("Supportability/Instrumented/{0}/{1}{2}", className
                                    .replace('/', '.'), m.getName(), m.getDescriptor()), 1.0f));
                        }
                    }
                }
            }
        };
        this.instrumentation = instrumentation;
        this.classWeaverService = new ClassWeaverService(this);
        this.matchVisitors.put(new TraceMatchVisitor(), InstrumentationContextManager.NO_OP_TRANSFORMER);
        this.matchVisitors.put(new GeneratedClassDetector(), InstrumentationContextManager.NO_OP_TRANSFORMER);
        final AgentConfig agentConfig = ServiceFactory.getConfigService().getDefaultAgentConfig();
        if (agentConfig.getValue("instrumentation.web_services.enabled", true)) {
            this.matchVisitors.put(new WebServiceVisitor(), InstrumentationContextManager.NO_OP_TRANSFORMER);
        }
        if (agentConfig.getValue("instrumentation.rest_annotations.enabled", true)) {
            final RestAnnotationVisitor rest = new RestAnnotationVisitor();
            this.matchVisitors.put(rest.getClassMatchVisitorFactory(), InstrumentationContextManager.NO_OP_TRANSFORMER);
            this.interfaceMatchVisitors.put(rest.getInterfaceMatchVisitorFactory(this), InstrumentationContextManager.NO_OP_TRANSFORMER);
        }
        if (agentConfig.getValue("instrumentation.spring_annotations.enabled", true)) {
            final SpringAnnotationVisitor rest2 = new SpringAnnotationVisitor();
            this.matchVisitors.put(rest2.getClassMatchVisitorFactory(), InstrumentationContextManager.NO_OP_TRANSFORMER);
        }
        if (agentConfig.getValue("instrumentation.servlet_annotations.enabled", true)) {
            this.matchVisitors.put(new ServletAnnotationVisitor(), InstrumentationContextManager.NO_OP_TRANSFORMER);
        }
        final Config instrumentationConfig = agentConfig.getClassTransformerConfig()
                .getInstrumentationConfig("com.newrelic.instrumentation.ejb-3.0");
        if (instrumentationConfig.getProperty("enabled", true)) {
            this.matchVisitors.put(new EJBAnnotationVisitor(), InstrumentationContextManager.NO_OP_TRANSFORMER);
        }
        this.matchVisitors.put(ServiceFactory.getJarCollectorService()
                .getSourceVisitor(), InstrumentationContextManager.NO_OP_TRANSFORMER);
        try {
            ApiImplementationUpdate.setup(this);
        } catch (Exception e) {
            Agent.LOG.log(Level.FINEST, e.toString(), e);
        }
    }

    public ClassWeaverService getClassWeaverService() {
        return this.classWeaverService;
    }

    public static InstrumentationContextManager create(final InstrumentationProxy instrumentation,
                                                       final boolean bootstrapClassloaderEnabled) throws Exception {
        final InstrumentationContextManager manager = new InstrumentationContextManager(instrumentation);
        final TraceClassTransformer traceTransformer = new TraceClassTransformer();
        final Runnable loadWeavedInstrumentation = manager.classWeaverService.registerInstrumentation();
        final boolean[] initialized = {false};
        final ClassLoaderClassTransformer classLoaderTransformer = new ClassLoaderClassTransformer(manager);
        final ClassFileTransformer transformer = new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                                    final ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException {
                if (className.startsWith("com/newrelic/agent/deps/org/objectweb/asm")
                        || className.startsWith("com/newrelic/deps")
                        || className.startsWith("com/newrelic/agent/tracers/")) {
                    return null;
                }
                if (!initialized[0] && className.startsWith("com/newrelic/")) {
                    return null;
                }
                if (loader == null) {
                    if (!bootstrapClassloaderEnabled) {
                        return null;
                    }
                    loader = ClassLoader.getSystemClassLoader();
                }
                final ClassReader reader = new ClassReader(classfileBuffer);
                if ((0x2200 & reader.getAccess()) != 0x0) {
                    manager.applyInterfaceVisitors(loader, classBeingRedefined, reader);
                    return null;
                }
                if (NewClassMarker.isNewWeaveClass(reader)) {
                    return null;
                }
                if (Utils.isJdkProxy(reader)) {
                    Agent.LOG.finest(MessageFormat.format("Instrumentation skipped by ''JDK proxy'' rule: {0}", className));
                    return null;
                }
                final InstrumentationContext context
                        = new InstrumentationContext(classfileBuffer, classBeingRedefined, protectionDomain);
                context.match(loader, classBeingRedefined, reader, manager.matchVisitors.keySet());
                if (context.isGenerated()) {
                    if (context.hasSourceAttribute()) {
                        Agent.LOG.finest(MessageFormat.format("Instrumentation skipped by ''generated'' rule: {0}", className));
                    } else {
                        Agent.LOG.finest(MessageFormat.format("Instrumentation skipped by ''no source'' rule: {0}", className));
                    }
                    return null;
                }
                if (!context.getMatches().isEmpty() && skipClass(reader)) {
                    Agent.LOG.finest(MessageFormat.format("Instrumentation skipped by ''class name'' rule: {0}", className));
                    return null;
                }
                for (final Map.Entry<ClassMatchVisitorFactory, OptimizedClassMatcher.Match> entry : context.getMatches()
                        .entrySet()) {
                    final ContextClassTransformer transformer = manager.matchVisitors.get(entry.getKey());
                    if (transformer != null && transformer != InstrumentationContextManager.NO_OP_TRANSFORMER) {
                        final byte[] bytes
                                = transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, context, entry
                                .getValue());
                        classfileBuffer = context.processTransformBytes(classfileBuffer, bytes);
                    } else {
                        Agent.LOG.fine("Unable to find a class transformer to process match " + entry.getValue());
                    }
                }
                if (context.isTracerMatch()) {
                    final byte[] bytes2
                            = traceTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, context, null);
                    classfileBuffer = context.processTransformBytes(classfileBuffer, bytes2);
                }
                if (context.isModified()) {
                    return manager.FinishClassTransformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer, context, null);
                }
                return null;
            }
        };
        instrumentation.addTransformer(transformer, true);
        manager.transformer = transformer;
        loadWeavedInstrumentation.run();
        classLoaderTransformer.start(instrumentation);
        initialized[0] = true;
        return manager;
    }

    private void applyInterfaceVisitors(final ClassLoader loader, final Class<?> classBeingRedefined,
                                        final ClassReader reader) {
        ClassVisitor cv = null;
        for (final ClassMatchVisitorFactory factory : this.interfaceMatchVisitors.keySet()) {
            cv = factory.newClassMatchVisitor(loader, classBeingRedefined, reader, cv, null);
        }
        if (cv != null) {
            reader.accept(cv, 1);
        }
    }

    private static boolean skipClass(final ClassReader reader) {
        for (final String interfaceName : reader.getInterfaces()) {
            if (InstrumentationContextManager.MARKER_INTERFACES_TO_SKIP.contains(interfaceName)) {
                return true;
            }
        }
        return false;
    }

    public void addContextClassTransformer(final ClassMatchVisitorFactory matchVisitor,
                                           ContextClassTransformer transformer) {
        if (transformer == null) {
            transformer = InstrumentationContextManager.NO_OP_TRANSFORMER;
        }
        this.matchVisitors.put(matchVisitor, transformer);
    }

    public void removeMatchVisitor(final ClassMatchVisitorFactory visitor) {
        this.matchVisitors.remove(visitor);
    }

    protected ClassVisitor addModifiedMethodAnnotation(final ClassVisitor cv, final InstrumentationContext context,
                                                       final ClassLoader loader) {
        return new ClassVisitor(327680, cv) {
            private String className;

            public void visit(final int version, final int access, final String name, final String signature,
                              final String superName, final String[] interfaces) {
                super.visit(version, access, this.className = name, signature, superName, interfaces);
            }

            public MethodVisitor visitMethod(final int access, final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                final Method method = new Method(name, desc);
                if (context.isModified(method) && loader != null) {
                    final TraceDetails traceDetails = context.getTraceInformation().getTraceAnnotations().get(method);
                    boolean dispatcher = false;
                    if (traceDetails != null) {
                        dispatcher = traceDetails.dispatcher();
                    }
                    final AnnotationVisitor av = mv.visitAnnotation(Type.getDescriptor(InstrumentedMethod.class), true);
                    av.visit("dispatcher", dispatcher);
                    final List<String> instrumentationNames = Lists.newArrayList();
                    final List<InstrumentationType> instrumentationTypes = Lists.newArrayList();
                    Level logLevel = Level.FINER;
                    if (traceDetails != null) {
                        if (traceDetails.instrumentationSourceNames() != null) {
                            instrumentationNames.addAll(traceDetails.instrumentationSourceNames());
                        }
                        if (traceDetails.instrumentationTypes() != null) {
                            for (final InstrumentationType type : traceDetails.instrumentationTypes()) {
                                if (type == InstrumentationType.WeaveInstrumentation) {
                                    instrumentationTypes.add(InstrumentationType.TracedWeaveInstrumentation);
                                } else {
                                    instrumentationTypes.add(type);
                                }
                            }
                        }
                        if (traceDetails.isCustom()) {
                            logLevel = Level.FINE;
                        }
                    }
                    final PointCut pointCut = context.getOldStylePointCut(method);
                    if (pointCut != null) {
                        instrumentationNames.add(pointCut.getClass().getName());
                        instrumentationTypes.add(InstrumentationType.Pointcut);
                    }
                    final Collection<String> instrumentationPackages = context.getMergeInstrumentationPackages(method);
                    if (instrumentationPackages != null && !instrumentationPackages.isEmpty()) {
                        for (final String current : instrumentationPackages) {
                            instrumentationNames.add(current);
                            instrumentationTypes.add(InstrumentationType.WeaveInstrumentation);
                        }
                    }
                    if (instrumentationNames.size() == 0) {
                        instrumentationNames.add("Unknown");
                        Agent.LOG.finest("Unknown instrumentation source for " + this.className + '.' + method);
                    }
                    if (instrumentationTypes.size() == 0) {
                        instrumentationTypes.add(InstrumentationType.Unknown);
                        Agent.LOG.finest("Unknown instrumentation type for " + this.className + '.' + method);
                    }
                    final AnnotationVisitor visitArrayName = av.visitArray("instrumentationNames");
                    for (final String current2 : instrumentationNames) {
                        visitArrayName.visit("", current2);
                    }
                    visitArrayName.visitEnd();
                    final AnnotationVisitor visitArrayType = av.visitArray("instrumentationTypes");
                    for (final InstrumentationType type2 : instrumentationTypes) {
                        visitArrayType.visitEnum("", Type.getDescriptor(InstrumentationType.class), type2.toString());
                    }
                    visitArrayType.visitEnd();
                    av.visitEnd();
                    if (Agent.LOG.isLoggable(logLevel)) {
                        Agent.LOG.log(logLevel,
                                "Instrumented " + Type.getObjectType(this.className).getClassName() + '.' + method
                                        + ", " + instrumentationTypes + ", " + instrumentationNames);
                    }
                }
                return mv;
            }
        };
    }

    protected ClassVisitor addModifiedClassAnnotation(final ClassVisitor cv, final InstrumentationContext context) {
        final AnnotationVisitor visitAnnotation = cv.visitAnnotation(Type.getDescriptor(InstrumentedClass.class), true);
        if (context.isUsingLegacyInstrumentation()) {
            visitAnnotation.visit("legacy", Boolean.TRUE);
        }
        if (context.hasModifiedClassStructure()) {
            visitAnnotation.visit("classStructureModified", Boolean.TRUE);
        }
        visitAnnotation.visitEnd();
        return cv;
    }

    public Instrumentation getInstrumentation() {
        return this.instrumentation;
    }

    public void setClassChecker(final ClassChecker classChecker) {
        this.classChecker = classChecker;
    }

    static {
        MARKER_INTERFACES_TO_SKIP
                = ImmutableSet.of("org/hibernate/proxy/HibernateProxy", "org/springframework/aop/SpringProxy");
        NO_OP_TRANSFORMER = new ContextClassTransformer() {
            public byte[] transform(final ClassLoader loader, final String className,
                                    final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain,
                                    final byte[] classfileBuffer, final InstrumentationContext context,
                                    final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
                return null;
            }
        };
        ANNOTATIONS_TO_REMOVE
                = ImmutableSet.of(Type.getDescriptor(InstrumentedClass.class), Type.getDescriptor(InstrumentedMethod.class), Type
                .getDescriptor(WeavedMethod.class));
    }

    private static class MarkWeaverMethodsVisitor extends ClassVisitor {
        private final InstrumentationContext context;

        public MarkWeaverMethodsVisitor(final ClassVisitor cv, final InstrumentationContext context) {
            super(327680, cv);
            this.context = context;
        }

        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
                                         final String[] exceptions) {
            final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final Collection<String> instrumentationTitles
                    = this.context.getMergeInstrumentationPackages(new Method(name, desc));
            if (instrumentationTitles != null && !instrumentationTitles.isEmpty()) {
                final AnnotationVisitor weavedAnnotation
                        = mv.visitAnnotation(Type.getDescriptor(WeavedMethod.class), true);
                final AnnotationVisitor visitArray = weavedAnnotation.visitArray("source");
                for (final String title : instrumentationTitles) {
                    visitArray.visit("", title);
                }
                visitArray.visitEnd();
                weavedAnnotation.visitEnd();
            }
            return mv;
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.config.ClassTransformerConfig;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.instrumentation.MethodBuilder;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.InstrumentationProxy;
import com.newrelic.agent.config.IBMUtils;
import com.newrelic.agent.service.ServiceFactory;
import java.lang.instrument.UnmodifiableClassException;
import java.util.logging.Level;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.lang.instrument.Instrumentation;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.DefaultClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcherBuilder;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.Type;

class ClassLoaderClassTransformer implements ContextClassTransformer
{
    public static final String NEWRELIC_CLASS_PREFIX = "com.newrelic.agent.";
    private static final String NEWRELIC_API_CLASS_PREFIX = "com.newrelic.api.agent.";
    private static final Type CLASSLOADER_TYPE;
    private static final Method LOAD_CLASS_METHOD;
    private static final Method LOAD_CLASS_RESOLVE_METHOD;
    private static final Set<Method> METHODS;
    private static final Method CHECK_PACKAGE_ACCESS_METHOD;
    private final ClassMatchVisitorFactory matcher;
    private final Set<String> classloadersToSkip;
    
    public ClassLoaderClassTransformer(final InstrumentationContextManager manager) {
        final OptimizedClassMatcherBuilder matcherBuilder = OptimizedClassMatcherBuilder.newBuilder();
        matcherBuilder.addClassMethodMatcher(new DefaultClassAndMethodMatcher(new ChildClassMatcher(ClassLoaderClassTransformer.CLASSLOADER_TYPE.getInternalName(), false), OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher(ClassLoaderClassTransformer.LOAD_CLASS_METHOD.getName(), ClassLoaderClassTransformer.LOAD_CLASS_METHOD.getDescriptor()), new ExactMethodMatcher(ClassLoaderClassTransformer.LOAD_CLASS_RESOLVE_METHOD.getName(), ClassLoaderClassTransformer.LOAD_CLASS_RESOLVE_METHOD.getDescriptor()))));
        manager.addContextClassTransformer(this.matcher = matcherBuilder.build(), this);
        final String agentClassloaderName = Type.getType(Agent.getClassLoader().getClass()).getInternalName();
        this.classloadersToSkip = ImmutableSet.of("com/ibm/oti/vm/BootstrapClassLoader", "sun/reflect/misc/MethodUtil", agentClassloaderName);
    }
    
    void start(final Instrumentation instrumentation) {
        final List<Class<?>> toRetransform = (List<Class<?>>)Lists.newArrayList();
        for (final Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            if (ClassLoader.class.isAssignableFrom(clazz) && !clazz.getName().startsWith("java.") && !clazz.getName().startsWith("sun.") && !this.classloadersToSkip.contains(Type.getType(clazz).getInternalName())) {
                toRetransform.add(clazz);
            }
        }
        if (!toRetransform.isEmpty()) {
            Agent.LOG.log(Level.FINER, "Retransforming {0}", new Object[] { toRetransform.toString() });
            for (final Class<?> classloader : toRetransform) {
                try {
                    instrumentation.retransformClasses(classloader);
                }
                catch (UnmodifiableClassException e2) {
                    Agent.LOG.log(Level.FINE, "classloader transformer: Error retransforming {0}", new Object[] { classloader.getName() });
                }
            }
        }
        if (ServiceFactory.getConfigService().getDefaultAgentConfig().getIbmWorkaroundEnabled()) {
            Agent.LOG.log(Level.FINE, "classloader transformer: skipping redefine of {0}. IBM SR {1}. java.runtime.version {2}", new Object[] { ClassLoader.class.getName(), IBMUtils.getIbmSRNumber(), System.getProperty("java.runtime.version") });
        }
        else {
            try {
                Agent.LOG.log(Level.FINER, "classloader transformer: Attempting to redefine {0}", new Object[] { ClassLoader.class });
                InstrumentationProxy.forceRedefinition(instrumentation, ClassLoader.class);
            }
            catch (Exception e) {
                Agent.LOG.log(Level.FINEST, (Throwable)e, "classloader transformer: Error redefining {0}", new Object[] { ClassLoader.class.getName() });
            }
            Agent.LOG.log(Level.FINE, "classloader transformer: {0} isClassLoaderModified = {1}", new Object[] { ClassLoader.class, isClassLoaderModified(ClassLoader.class) });
        }
    }
    
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer, final InstrumentationContext context, final OptimizedClassMatcher.Match match) throws IllegalClassFormatException {
        if (this.classloadersToSkip.contains(className)) {
            Agent.LOG.log(Level.FINEST, "classloader transformer: classloadersToSkip contains {0}", new Object[] { className });
            return null;
        }
        try {
            if (match.isClassAndMethodMatch()) {
                return this.transformBytes(className, classfileBuffer);
            }
        }
        catch (Exception ex) {
            Agent.LOG.log(Level.FINER, (Throwable)ex, "classloader transformer: Error transforming {0}", new Object[] { className });
        }
        return null;
    }
    
    byte[] transformBytes(final String className, final byte[] classfileBuffer) {
        final ClassReader reader = new ClassReader(classfileBuffer);
        final ClassWriter writer = new ClassWriter(1);
        reader.accept(new ClassLoaderClassVisitor(writer), 8);
        Agent.LOG.log(Level.FINER, "class transformer: Patching {0}", new Object[] { className });
        return writer.toByteArray();
    }
    
    public static boolean isClassLoaderModified(final Class<ClassLoader> classLoaderClass) {
        return classLoaderClass.getAnnotation(ModifiedClassloader.class) != null;
    }
    
    static {
        CLASSLOADER_TYPE = Type.getType(ClassLoader.class);
        LOAD_CLASS_METHOD = new Method("loadClass", Type.getType(Class.class), new Type[] { Type.getType(String.class) });
        LOAD_CLASS_RESOLVE_METHOD = new Method("loadClass", Type.getType(Class.class), new Type[] { Type.getType(String.class), Type.getType(Boolean.TYPE) });
        METHODS = ImmutableSet.of(ClassLoaderClassTransformer.LOAD_CLASS_METHOD, ClassLoaderClassTransformer.LOAD_CLASS_RESOLVE_METHOD);
        CHECK_PACKAGE_ACCESS_METHOD = new Method("checkPackageAccess", Type.VOID_TYPE, new Type[] { Type.getType(Class.class), Type.getType(ProtectionDomain.class) });
    }
    
    private class ClassLoaderClassVisitor extends ClassVisitor
    {
        Set<Method> methods;
        
        public ClassLoaderClassVisitor(final ClassVisitor cv) {
            super(327680, cv);
            this.methods = ClassLoaderClassTransformer.METHODS;
        }
        
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            final AnnotationVisitor visitAnnotation = this.visitAnnotation(Type.getDescriptor(ModifiedClassloader.class), true);
            visitAnnotation.visitEnd();
            if (ClassLoaderClassTransformer.CLASSLOADER_TYPE.getInternalName().equals(name)) {
                this.methods = ImmutableSet.of(ClassLoaderClassTransformer.LOAD_CLASS_METHOD);
                Agent.LOG.log(Level.FINEST, "class transformer: Updated method matcher for {0}", new Object[] { name });
            }
        }
        
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final Method method = new Method(name, desc);
            if (this.methods.contains(method)) {
                mv = new AdviceAdapter(327680, mv, access, name, desc) {
                    Label startFinallyLabel;
                    
                    protected void onMethodEnter() {
                        this.startFinallyLabel = this.newLabel();
                        this.loadArg(0);
                        this.mv.visitLdcInsn("com.newrelic.api.agent.");
                        this.mv.visitMethodInsn(182, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
                        this.mv.visitJumpInsn(153, this.startFinallyLabel);
                        this.loadNewRelicClass();
                        this.visitLabel(this.startFinallyLabel);
                    }
                    
                    public void visitMaxs(final int maxStack, final int maxLocals) {
                        final Label endFinallyLabel = new Label();
                        super.visitTryCatchBlock(this.startFinallyLabel, endFinallyLabel, endFinallyLabel, Type.getType(ClassNotFoundException.class).getInternalName());
                        super.visitLabel(endFinallyLabel);
                        this.onMethodExit(191);
                        super.visitMaxs(maxStack, maxLocals);
                    }
                    
                    protected void onMethodExit(final int opcode) {
                        if (opcode == 191) {
                            this.loadArg(0);
                            this.mv.visitLdcInsn("com.newrelic.agent.");
                            this.mv.visitMethodInsn(182, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
                            final Label skip = this.newLabel();
                            this.mv.visitJumpInsn(153, skip);
                            this.loadNewRelicClass();
                            this.mv.visitLabel(skip);
                            this.mv.visitInsn(191);
                        }
                    }
                    
                    private void loadNewRelicClass() {
                        final MethodBuilder methodBuilder = new MethodBuilder(this, access);
                        methodBuilder.loadInvocationHandlerFromProxy();
                        this.mv.visitLdcInsn("CLASSLOADER");
                        this.mv.visitInsn(1);
                        this.mv.visitInsn(1);
                        methodBuilder.invokeInvocationHandlerInterface(false);
                        this.checkCast(Type.getType(ClassLoader.class));
                        final Label isNRClassLoader = this.newLabel();
                        this.dup();
                        this.loadThis();
                        this.ifCmp(ClassLoaderClassTransformer.CLASSLOADER_TYPE, 153, isNRClassLoader);
                        this.loadArg(0);
                        this.mv.visitMethodInsn(182, "java/lang/ClassLoader", ClassLoaderClassTransformer.LOAD_CLASS_METHOD.getName(), ClassLoaderClassTransformer.LOAD_CLASS_METHOD.getDescriptor(), false);
                        this.mv.visitInsn(176);
                        this.mv.visitLabel(isNRClassLoader);
                        this.pop();
                    }
                };
            }
            else if (ClassLoaderClassTransformer.CHECK_PACKAGE_ACCESS_METHOD.equals(method) && System.getSecurityManager() != null) {
                final ClassTransformerConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig().getClassTransformerConfig();
                if (config.isGrantPackageAccess()) {
                    mv = new AdviceAdapter(327680, mv, access, name, desc) {
                        protected void onMethodEnter() {
                            this.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "instrumentation", BridgeUtils.INSTRUMENTATION_TYPE);
                            this.loadArg(0);
                            this.invokeInterface(BridgeUtils.INSTRUMENTATION_TYPE, new Method("isWeaveClass", Type.BOOLEAN_TYPE, new Type[] { Type.getType(Class.class) }));
                            final Label skip = this.newLabel();
                            this.ifZCmp(153, skip);
                            this.visitInsn(177);
                            this.visitLabel(skip);
                            super.onMethodEnter();
                        }
                    };
                }
            }
            return mv;
        }
    }
}

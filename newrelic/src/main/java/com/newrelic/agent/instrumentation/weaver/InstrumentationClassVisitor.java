// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.instrumentation.tracing.Annotation;
import com.newrelic.agent.instrumentation.InstrumentationType;
import com.newrelic.agent.instrumentation.tracing.TraceDetailsBuilder;
import com.newrelic.agent.deps.org.objectweb.asm.commons.JSRInlinerAdapter;
import com.newrelic.api.agent.Trace;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.instrumentation.InstrumentationImpl;
import com.newrelic.agent.bridge.reflect.ClassReflection;
import com.newrelic.api.agent.Logger;
import com.newrelic.agent.instrumentation.context.CurrentTransactionRewriter;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.ClassWriter;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.bridge.ObjectFieldManager;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import java.util.Collections;
import com.newrelic.api.agent.weaver.SkipIfPresent;
import java.util.Collection;
import java.util.logging.Level;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.api.agent.weaver.CatchAndLog;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.deps.org.objectweb.asm.tree.FieldNode;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InnerClassNode;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

class InstrumentationClassVisitor extends ClassVisitor implements WeavedClassInfo
{
    private static final String OBJECT_FIELDS_FIELD_NAME = "objectFieldManager";
    private static final Method INITIALIZE_FIELDS_METHOD;
    private static final Method GET_FIELD_CONTAINER_METHOD;
    private final String className;
    final Set<InnerClassNode> innerClasses;
    private final Map<String, FieldNode> newFields;
    private final Map<String, FieldNode> existingFields;
    private final Set<Method> weavedMethods;
    private final Set<Method> catchAndLogMethods;
    private final Map<Method, TraceDetails> tracedMethods;
    private MethodNode staticConstructor;
    private final Map<Method, MethodNode> constructors;
    private final WeaveMatchTypeAccessor weaveAnnotation;
    private final InstrumentationPackage instrumentationPackage;
    private boolean hasNewInstanceField;
    private boolean skipIfPresent;
    private final String superName;
    
    static InstrumentationClassVisitor getInstrumentationClass(final InstrumentationPackage instrumentationPackage, final byte[] bytes) {
        final ClassReader reader = new ClassReader(bytes);
        final InstrumentationClassVisitor cv = new InstrumentationClassVisitor(instrumentationPackage, reader.getClassName(), reader.getSuperName());
        reader.accept(cv, 6);
        return cv;
    }
    
    private InstrumentationClassVisitor(final InstrumentationPackage instrumentationPackage, final String className, final String superName) {
        super(327680);
        this.innerClasses = (Set<InnerClassNode>)Sets.newHashSet();
        this.newFields = (Map<String, FieldNode>)Maps.newHashMap();
        this.existingFields = (Map<String, FieldNode>)Maps.newHashMap();
        this.weavedMethods = (Set<Method>)Sets.newHashSet();
        this.catchAndLogMethods = (Set<Method>)Sets.newHashSet();
        this.tracedMethods = (Map<Method, TraceDetails>)Maps.newHashMap();
        this.constructors = (Map<Method, MethodNode>)Maps.newHashMap();
        this.weaveAnnotation = new WeaveMatchTypeAccessor();
        this.className = className;
        this.superName = superName;
        this.instrumentationPackage = instrumentationPackage;
    }
    
    private String getFieldContainerKeyName(final String className) {
        return this.instrumentationPackage.implementationTitle + ':' + className;
    }
    
    public static String getFieldContainerClassName(final String ownerClass) {
        return "weave/newrelic/" + ownerClass + "$NewRelicFields";
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public boolean isSkipIfPresent() {
        return this.skipIfPresent;
    }
    
    public MatchType getMatchType() {
        return this.weaveAnnotation.getMatchType();
    }
    
    public boolean isWeaveInstrumentation() {
        return this.weaveAnnotation.getMatchType() != null;
    }
    
    private ClassVisitor verifyWeaveConstructors(final ClassVisitor cv) {
        return new ClassVisitor(327680, cv) {
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if ("<init>".equals(name) || "<clinit>".equals(name)) {
                    mv = new MethodVisitor(327680, mv) {
                        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                            if (BridgeUtils.WEAVER_TYPE.getInternalName().equals(owner)) {
                                throw new IllegalInstructionException("Weave instrumentation constructors must not invoke " + BridgeUtils.WEAVER_TYPE.getClassName() + '.' + WeaveUtils.CALL_ORIGINAL_METHOD);
                            }
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    };
                }
                return mv;
            }
        };
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ((0x40 & access) == 0x40) {
            return mv;
        }
        if (this.isWeaveInstrumentation() && "<clinit>".equals(name)) {
            return this.staticConstructor = new MethodNode(access, name, desc, signature, exceptions);
        }
        final Method method = new Method(name, desc);
        mv = new TraceAnnotationVisitor(mv, method);
        mv = this.trackCatchAndLogAnnotations(mv, method);
        if ((access & 0x400) != 0x0) {
            return mv;
        }
        if ("<init>".equals(name)) {
            final MethodNode node = (MethodNode)(mv = new MethodNode(access, name, desc, signature, exceptions));
            this.constructors.put(method, node);
        }
        return new MethodVisitor(327680, mv) {
            public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                if (MergeMethodVisitor.isOriginalMethodInvocation(owner, name, desc)) {
                    InstrumentationClassVisitor.this.weavedMethods.add(method);
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        };
    }
    
    private MethodVisitor trackCatchAndLogAnnotations(final MethodVisitor mv, final Method method) {
        return new MethodVisitor(327680, mv) {
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (Type.getDescriptor(CatchAndLog.class).equals(desc)) {
                    InstrumentationClassVisitor.this.catchAndLogMethods.add(method);
                }
                return super.visitAnnotation(desc, visible);
            }
        };
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (!this.isWeaveInstrumentation()) {
            return super.visitField(access, name, desc, signature, value);
        }
        if ((access & 0x8) == 0x8 && name.equals("serialVersionUID")) {
            final String message = String.format("A static serialVersionUID field was declared in weaved class %s.  The weaver does not support accessing serialVersionUID from weaeved classes.", this.className);
            this.getLogger().log(Level.SEVERE, message);
            throw new IllegalInstructionException(message);
        }
        final FieldNode field = new FieldNode(327680, access, name, desc, signature, value) {
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (WeaveUtils.NEW_FIELD_ANNOTATION_DESCRIPTOR.equals(desc)) {
                    InstrumentationClassVisitor.this.newFields.put(this.name, this);
                    if ((this.access & 0x8) == 0x0) {
                        InstrumentationClassVisitor.this.hasNewInstanceField = true;
                    }
                }
                return super.visitAnnotation(desc, visible);
            }
        };
        if ((access & 0x18) == 0x18) {
            this.newFields.put(name, field);
        }
        else {
            this.existingFields.put(name, field);
        }
        return field;
    }
    
    public void visitEnd() {
        super.visitEnd();
        this.existingFields.keySet().removeAll(this.newFields.keySet());
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        if (Type.getDescriptor(SkipIfPresent.class).equals(desc)) {
            this.skipIfPresent = true;
        }
        return this.weaveAnnotation.visitAnnotation(desc, visible, super.visitAnnotation(desc, visible));
    }
    
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        this.innerClasses.add(new InnerClassNode(name, outerName, innerName, access));
    }
    
    public Set<Method> getWeavedMethods() {
        return Collections.unmodifiableSet((Set<? extends Method>)this.weavedMethods);
    }
    
    public Map<Method, TraceDetails> getTracedMethods() {
        return Collections.unmodifiableMap((Map<? extends Method, ? extends TraceDetails>)this.tracedMethods);
    }
    
    public Map<String, FieldNode> getNewFields() {
        return this.newFields;
    }
    
    public Collection<FieldNode> getReferencedFields() {
        return Collections.unmodifiableCollection((Collection<? extends FieldNode>)this.existingFields.values());
    }
    
    public void generateInitializeFieldHandlerInstructions(final GeneratorAdapter generatorAdapter) {
        if (!this.isWeaveInstrumentation()) {
            return;
        }
        generatorAdapter.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "objectFieldManager", Type.getType(ObjectFieldManager.class));
        generatorAdapter.push(this.getFieldContainerKeyName(this.className));
        generatorAdapter.loadThis();
        final Type fieldContainerType = Type.getObjectType(getFieldContainerClassName(this.className));
        generatorAdapter.newInstance(fieldContainerType);
        generatorAdapter.dup();
        generatorAdapter.invokeConstructor(fieldContainerType, new Method("<init>", Type.VOID_TYPE, new Type[0]));
        generatorAdapter.invokeInterface(Type.getType(ObjectFieldManager.class), InstrumentationClassVisitor.INITIALIZE_FIELDS_METHOD);
    }
    
    private void generateVisitFieldInstructions(final GeneratorAdapter generator, final int opcode, final String fieldName, final String fieldDesc) {
        final Type fieldContainerType = Type.getObjectType(getFieldContainerClassName(this.className));
        final Type fieldType = Type.getType(fieldDesc);
        if (opcode == 180) {
            generator.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "objectFieldManager", Type.getType(ObjectFieldManager.class));
            generator.swap();
            generator.push(this.getFieldContainerKeyName(this.className));
            generator.swap();
            generator.invokeInterface(Type.getType(ObjectFieldManager.class), InstrumentationClassVisitor.GET_FIELD_CONTAINER_METHOD);
            this.verifyFieldContainerIsNotNull(generator);
            generator.checkCast(fieldContainerType);
            generator.getField(fieldContainerType, fieldName, fieldType);
        }
        else if (opcode == 181) {
            final int fieldValue = generator.newLocal(fieldType);
            generator.storeLocal(fieldValue);
            generator.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "objectFieldManager", Type.getType(ObjectFieldManager.class));
            generator.swap();
            generator.push(this.getFieldContainerKeyName(this.className));
            generator.swap();
            generator.invokeInterface(Type.getType(ObjectFieldManager.class), InstrumentationClassVisitor.GET_FIELD_CONTAINER_METHOD);
            this.verifyFieldContainerIsNotNull(generator);
            generator.checkCast(fieldContainerType);
            generator.loadLocal(fieldValue);
            generator.putField(fieldContainerType, fieldName, Type.getType(fieldDesc));
        }
        else if (opcode == 178) {
            generator.getStatic(fieldContainerType, fieldName, fieldType);
        }
        else if (opcode == 179) {
            generator.putStatic(fieldContainerType, fieldName, Type.getType(fieldDesc));
        }
    }
    
    private void verifyFieldContainerIsNotNull(final GeneratorAdapter generator) {
        generator.dup();
        final Label skip = generator.newLabel();
        generator.ifNonNull(skip);
        generator.throwException(Type.getType(NullPointerException.class), "The object field container was null for " + this.getClassName());
        generator.visitLabel(skip);
    }
    
    public MethodVisitor getMethodVisitor(final String className, final MethodVisitor codeVisitor, final int access, final Method method) {
        if (!this.isWeaveInstrumentation()) {
            return codeVisitor;
        }
        MethodVisitor mv = codeVisitor;
        if (!this.newFields.isEmpty()) {
            mv = new GeneratorAdapter(327680, mv, access, method.getName(), method.getDescriptor()) {
                public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
                    final FieldNode fieldNode = InstrumentationClassVisitor.this.newFields.get(name);
                    if (null != fieldNode) {
                        InstrumentationClassVisitor.this.generateVisitFieldInstructions(this, opcode, name, desc);
                    }
                    else {
                        super.visitFieldInsn(opcode, owner, name, desc);
                    }
                }
            };
        }
        if (!this.existingFields.isEmpty()) {
            mv = new GeneratorAdapter(327680, mv, access, method.getName(), method.getDescriptor()) {
                public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
                    if (className.equals(owner) && !InstrumentationClassVisitor.this.newFields.containsKey(name)) {
                        final FieldNode existingField = InstrumentationClassVisitor.this.existingFields.get(name);
                        if (existingField == null) {
                            throw new IllegalInstructionException("Weaved instrumentation method " + method + " is attempting to access field " + name + " of type " + desc + " which does not exist.  This field may need to be marked with " + NewField.class.getName());
                        }
                        if (!desc.equals(existingField.desc)) {
                            throw new IllegalInstructionException("Weaved instrumentation method " + method + " accesses field " + name + " of type " + desc + ", but the actual type is " + existingField.desc);
                        }
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        }
        return mv;
    }
    
    public MethodVisitor getConstructorMethodVisitor(MethodVisitor mv, final String className, final int access, final String name, final String desc) {
        if (this.hasNewInstanceField) {
            mv = new AdviceAdapter(327680, mv, access, name, desc) {
                protected void onMethodExit(final int opcode) {
                    if (opcode != 191) {
                        final Label start = this.newLabel();
                        final Label end = this.newLabel();
                        final Label handler = this.newLabel();
                        this.visitLabel(start);
                        InstrumentationClassVisitor.this.generateInitializeFieldHandlerInstructions(this);
                        this.goTo(end);
                        this.visitLabel(handler);
                        this.pop();
                        this.visitLabel(end);
                        this.visitTryCatchBlock(start, end, handler, Type.getType(Throwable.class).getInternalName());
                    }
                    super.onMethodExit(opcode);
                }
            };
        }
        return mv;
    }
    
    public static void performSecondPassProcessing(final InstrumentationPackage instrumentationPackage, final Map<String, InstrumentationClassVisitor> instrumentationClasses, final Map<String, WeavedClassInfo> weaveClasses, final Map<String, byte[]> classBytes, final List<String> newClassLoadOrder) {
        for (final Map.Entry<String, InstrumentationClassVisitor> entry : instrumentationClasses.entrySet()) {
            final byte[] bytes = classBytes.get(entry.getKey());
            final ClassReader reader = new ClassReader(bytes);
            final ClassWriter writer = new ClassWriter(1);
            final ClassVisitor cv = entry.getValue().createSecondPassVisitor(instrumentationPackage, classBytes, newClassLoadOrder, weaveClasses, reader, writer);
            reader.accept(cv, 8);
            classBytes.put(entry.getKey(), writer.toByteArray());
        }
    }
    
    ClassVisitor createSecondPassVisitor(final InstrumentationPackage instrumentationPackage, final Map<String, byte[]> classBytes, final List<String> newClassLoadOrder, final Map<String, WeavedClassInfo> weaveClasses, final ClassReader reader, ClassVisitor cv) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null && !this.isWeaveInstrumentation()) {
            cv = this.handleElevatePermissions(cv, classBytes);
        }
        cv = new LogApiCallsVisitor(instrumentationPackage, cv);
        cv = new RegisterClosableInstrumentationVisitor(instrumentationPackage, cv);
        cv = fixInvocationInstructions(weaveClasses, cv);
        cv = enforceTracedMethodsAccessingTracedMethod(cv, this.getLogger(), this.className, this.tracedMethods.keySet());
        if (this.isWeaveInstrumentation()) {
            if (!this.skipIfPresent) {}
            this.createFieldContainerClass(classBytes);
            cv = new LogWeavedMethodInvocationsVisitor(instrumentationPackage, cv);
            cv = removeTraceAnnotationsFromMethods(cv);
            cv = this.verifyWeaveConstructors(cv);
            cv = removeDefaultConstructors(this.constructors, cv);
            if (!this.catchAndLogMethods.isEmpty()) {
                this.getLogger().log(Level.SEVERE, "{0} is a weaved class but the following methods are marked with the {1} annotation: {2}", new Object[] { this.className, CatchAndLog.class.getSimpleName(), this.catchAndLogMethods });
            }
        }
        else {
            this.verifyNewClass();
            final NewClassDependencyVisitor dependencyCV = new NewClassDependencyVisitor(327680, cv, newClassLoadOrder);
            cv = NewClassMarker.getVisitor(dependencyCV, instrumentationPackage.implementationTitle, Float.toString(instrumentationPackage.implementationVersion));
            cv = CurrentTransactionRewriter.rewriteCurrentTransactionReferences(cv, reader);
            if (!this.catchAndLogMethods.isEmpty()) {
                cv = this.rewriteCatchAndLogMethods(cv);
            }
        }
        return cv;
    }
    
    private ClassVisitor rewriteCatchAndLogMethods(final ClassVisitor cv) {
        return new ClassVisitor(327680, cv) {
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (InstrumentationClassVisitor.this.catchAndLogMethods.contains(new Method(name, desc))) {
                    if (Type.VOID_TYPE.equals(Type.getType(desc).getReturnType())) {
                        mv = InstrumentationClassVisitor.catchAndLogMethodExceptions(InstrumentationClassVisitor.this.instrumentationPackage.getImplementationTitle(), InstrumentationClassVisitor.this.className, access, name, desc, mv);
                    }
                    else {
                        InstrumentationClassVisitor.this.getLogger().log(Level.SEVERE, "{0}.{1}{2} is marked with {3}, but only void return types are supported.", new Object[] { InstrumentationClassVisitor.this.className, name, desc, CatchAndLog.class.getSimpleName() });
                    }
                }
                return mv;
            }
        };
    }
    
    static AdviceAdapter catchAndLogMethodExceptions(final String instrumentationTitle, final String className, final int access, final String name, final String desc, final MethodVisitor mv) {
        return new AdviceAdapter(327680, mv, access, name, desc) {
            Label start = this.newLabel();
            Label end = this.newLabel();
            Label handler = this.newLabel();
            
            protected void onMethodEnter() {
                super.onMethodEnter();
                this.visitLabel(this.start);
            }
            
            public void visitMaxs(final int maxStack, final int maxLocals) {
                super.visitLabel(this.handler);
                final int throwableLocal = this.newLocal(Type.getType(Throwable.class));
                this.storeLocal(throwableLocal);
                final Runnable throwableMessage = new Runnable() {
                    public void run() {
                        AdviceAdapter.this.loadLocal(throwableLocal);
                    }
                };
                BridgeUtils.getLogger(this).logToChild(instrumentationTitle, Level.FINE, "{0}.{1}{2} threw an exception: {3}", new Object[] { className, name, desc, throwableMessage });
                BridgeUtils.loadLogger(this);
                this.getStatic(Type.getType(Level.class), Level.FINEST.getName(), Type.getType(Level.class));
                this.loadLocal(throwableLocal);
                this.push("Exception stack:");
                this.visitInsn(1);
                BridgeUtils.getLoggerBuilder(this, false).build().log(Level.FINEST, (Throwable)null, (String)null, new Object[0]);
                this.visitInsn(177);
                super.visitLabel(this.end);
                super.visitTryCatchBlock(this.start, this.end, this.handler, Type.getInternalName(Throwable.class));
                super.visitMaxs(maxStack, maxLocals);
            }
        };
    }
    
    private ClassVisitor handleElevatePermissions(final ClassVisitor cv, final Map<String, byte[]> classBytes) {
        return new ClassVisitor(327680, cv) {
            private final ReflectionHelper reflection = ReflectionHelper.get();
            
            public MethodVisitor visitMethod(final int access, final String methodName, final String methodDesc, final String signature, final String[] exceptions) {
                final MethodVisitor mv = super.visitMethod(access, methodName, methodDesc, signature, exceptions);
                return new GeneratorAdapter(327680, mv, access, methodName, methodDesc) {
                    public void visitLdcInsn(final Object cst) {
                        if (cst instanceof Type && !InstrumentationClassVisitor.this.getClassName().equals(((Type)cst).getInternalName())) {
                            this.loadClass((Type)cst);
                        }
                        else {
                            super.visitLdcInsn(cst);
                        }
                    }
                    
                    private void loadClass(final Type typeToLoad) {
                        super.visitLdcInsn(Type.getObjectType(InstrumentationClassVisitor.this.className));
                        super.invokeStatic(Type.getType(ClassReflection.class), new Method("getClassLoader", "(Ljava/lang/Class;)Ljava/lang/ClassLoader;"));
                        this.push(typeToLoad.getClassName());
                        super.invokeStatic(Type.getType(ClassReflection.class), new Method("loadClass", "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;"));
                    }
                    
                    public void visitTypeInsn(final int opcode, final String type) {
                        final Type objectType = Type.getObjectType(type);
                        if (193 == opcode) {
                            this.loadClass(objectType);
                            super.swap();
                            super.invokeVirtual(Type.getType(Class.class), new Method("isInstance", "(Ljava/lang/Object;)Z"));
                        }
                        else if (192 == opcode) {
                            if (!type.startsWith("java/")) {
                                this.addWeaveClass(objectType);
                            }
                            super.visitTypeInsn(opcode, type);
                        }
                        else {
                            super.visitTypeInsn(opcode, type);
                        }
                    }
                    
                    private void addWeaveClass(final Type objectType) {
                        ((InstrumentationImpl)AgentBridge.instrumentation).addWeaveClass(objectType);
                    }
                    
                    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                        if (ClassVisitor.this.reflection.process(owner, name, desc, this)) {
                            return;
                        }
                        this.addWeaveClass(Type.getObjectType(owner));
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                    }
                };
            }
        };
    }
    
    static ClassVisitor enforceTracedMethodsAccessingTracedMethod(final ClassVisitor cv, final IAgentLogger logger, final String className, final Set<Method> tracedMethods) {
        return new ClassVisitor(327680, cv) {
            public MethodVisitor visitMethod(final int access, final String methodName, final String methodDesc, final String signature, final String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, methodName, methodDesc, signature, exceptions);
                final Method method = new Method(methodName, methodDesc);
                if (!tracedMethods.contains(method)) {
                    mv = new MethodVisitor(327680, mv) {
                        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
                            super.visitFieldInsn(opcode, owner, name, desc);
                            if (owner.equals(BridgeUtils.TRACED_METHOD_TYPE.getInternalName())) {
                                logger.severe("Error in " + className + '.' + method);
                                throw new IllegalInstructionException(BridgeUtils.TRACED_METHOD_TYPE.getClassName() + '.' + name + " can only be called from a traced method");
                            }
                        }
                        
                        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                            if (BridgeUtils.isAgentType(owner) && "getTracedMethod".equals(name)) {
                                logger.severe("Error in " + className + '.' + method);
                                throw new IllegalInstructionException(BridgeUtils.PUBLIC_AGENT_TYPE.getClassName() + '.' + "getTracedMethod" + " can only be called from a traced method");
                            }
                        }
                    };
                }
                return mv;
            }
        };
    }
    
    static ClassVisitor removeDefaultConstructors(final Map<Method, MethodNode> constructors, final ClassVisitor cv) {
        return new ClassVisitor(327680, cv) {
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                final MethodNode methodNode = constructors.get(new Method(name, desc));
                if (this.isDefaultConstructor(methodNode)) {
                    return new MethodVisitor(327680) {};
                }
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
            
            private boolean isDefaultConstructor(final MethodNode methodNode) {
                return methodNode != null && methodNode.instructions.getLast().getPrevious().getOpcode() == 183;
            }
        };
    }
    
    void verifyNewClass() throws IllegalInstructionException {
        if (!this.newFields.isEmpty()) {
            this.getLogger().severe("Non-weave class " + this.className + " cannot use @NewField for fields " + this.newFields.keySet());
            throw new IllegalInstructionException(BridgeUtils.WEAVER_TYPE.getClassName() + " is not a weaved method but uses the @NewField annotation");
        }
        if (!this.weavedMethods.isEmpty()) {
            this.getLogger().severe("Error in " + this.className + " methods " + this.weavedMethods);
            throw new IllegalInstructionException(BridgeUtils.WEAVER_TYPE.getClassName() + '.' + WeaveUtils.CALL_ORIGINAL_METHOD + " can only be called from a weaved method");
        }
        if (!this.tracedMethods.isEmpty()) {
            this.getLogger().severe("Error in " + this.className + " methods " + this.tracedMethods.keySet());
            throw new IllegalInstructionException("The Trace annotation can only be used on existing methods in existing classes");
        }
    }
    
    private static ClassVisitor removeTraceAnnotationsFromMethods(final ClassVisitor cv) {
        return new ClassVisitor(327680, cv) {
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                return new MethodVisitor(327680, super.visitMethod(access, name, desc, signature, exceptions)) {
                    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                        if (Type.getDescriptor(Trace.class).equals(desc)) {
                            return null;
                        }
                        return super.visitAnnotation(desc, visible);
                    }
                };
            }
        };
    }
    
    private static ClassVisitor fixInvocationInstructions(final Map<String, WeavedClassInfo> weaveClasses, final ClassVisitor cv) {
        return new ClassVisitor(327680, cv) {
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                mv = new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
                return new MethodVisitor(327680, mv) {
                    public void visitMethodInsn(int opcode, final String owner, final String name, final String desc, boolean itf) {
                        if (opcode == 182) {
                            final WeavedClassInfo weaveInfo = weaveClasses.get(owner);
                            if (weaveInfo != null && MatchType.Interface.equals((Object)weaveInfo.getMatchType())) {
                                itf = true;
                                opcode = 185;
                            }
                        }
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                    }
                };
            }
        };
    }
    
    private void createFieldContainerClass(final Map<String, byte[]> classBytes) {
        if (this.newFields.isEmpty()) {
            return;
        }
        final String className = getFieldContainerClassName(this.className);
        AgentBridge.objectFieldManager.createClassObjectFields(this.getFieldContainerKeyName(this.className));
        final ClassWriter cw = new ClassWriter(1);
        cw.visit(49, 33, className, null, "java/lang/Object", new String[0]);
        for (final FieldNode fieldNode : this.newFields.values()) {
            final FieldNode field = fieldNode;
            fieldNode.access |= 0x1;
            final FieldNode fieldNode2 = field;
            fieldNode2.access &= 0xFFFFFFE9;
            cw.visitField(field.access, field.name, field.desc, field.signature, field.value);
        }
        if (this.staticConstructor != null) {
            MethodVisitor mv = cw.visitMethod(9, this.staticConstructor.name, this.staticConstructor.desc, null, null);
            mv = new MethodVisitor(327680, mv) {
                public void visitFieldInsn(final int opcode, String owner, final String name, final String desc) {
                    if (owner.equals(InstrumentationClassVisitor.this.className) && InstrumentationClassVisitor.this.newFields.containsKey(name)) {
                        owner = className;
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
            this.staticConstructor.instructions.accept(mv);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        cw.visitEnd();
        this.getLogger().finest("Generated field container " + className);
        classBytes.put(className, cw.toByteArray());
    }
    
    private IAgentLogger getLogger() {
        return this.instrumentationPackage.getLogger();
    }
    
    public String getSuperName() {
        return this.superName;
    }
    
    static {
        INITIALIZE_FIELDS_METHOD = new Method("initializeFields", Type.VOID_TYPE, new Type[] { Type.getType(String.class), Type.getType(Object.class), Type.getType(Object.class) });
        GET_FIELD_CONTAINER_METHOD = new Method("getFieldContainer", Type.getType(Object.class), new Type[] { Type.getType(String.class), Type.getType(Object.class) });
    }
    
    private class TraceAnnotationVisitor extends MethodVisitor
    {
        private final Method method;
        
        public TraceAnnotationVisitor(final MethodVisitor mv, final Method method) {
            super(327680, mv);
            this.method = method;
        }
        
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            final AnnotationVisitor av = super.visitAnnotation(desc, visible);
            if (Type.getDescriptor(Trace.class).equals(desc)) {
                final TraceDetailsBuilder builder = TraceDetailsBuilder.newBuilder().setInstrumentationType(InstrumentationType.WeaveInstrumentation).setInstrumentationSourceName(InstrumentationClassVisitor.this.instrumentationPackage.implementationTitle);
                return new Annotation(av, desc, builder) {
                    public void visitEnd() {
                        InstrumentationClassVisitor.this.tracedMethods.put(TraceAnnotationVisitor.this.method, this.getTraceDetails(false));
                        super.visitEnd();
                    }
                };
            }
            return av;
        }
    }
}

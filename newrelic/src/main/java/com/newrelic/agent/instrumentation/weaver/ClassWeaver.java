// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.Attribute;
import com.newrelic.agent.deps.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.org.objectweb.asm.commons.MethodCallInlinerAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.Label;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.JSRInlinerAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InnerClassNode;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import java.util.Iterator;
import java.util.Collection;
import java.util.logging.Level;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.instrumentation.tracing.TraceDetails;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.HashSet;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import com.newrelic.agent.util.asm.ClassStructure;
import com.newrelic.agent.instrumentation.context.InstrumentationContext;
import com.newrelic.agent.deps.org.objectweb.asm.tree.FieldNode;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Remapper;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

class ClassWeaver extends ClassVisitor
{
    private static final Remapper NO_OP_REMAPPER;
    private final Set<Method> originalMethods;
    private final String className;
    private final Map<Method, MergeMethodVisitor> methods;
    private final Map<String, FieldNode> newFields;
    private final Map<String, FieldNode> existingFields;
    private final Verifier verifier;
    private final MixinClassVisitor mixinClassVisitor;
    private final Map<Method, MergeMethodVisitor> newMethods;
    private boolean firstField;
    private final InstrumentationContext context;
    private final InstrumentationPackage instrumentationPackage;
    private int version;
    private static final String MAGIC_KEY_FOR_CONSTRUCTOR_INLINE = "____INLINE_ME____";
    
    public ClassWeaver(final ClassVisitor cv, final MixinClassVisitor mixinClassVisitor, final String className, final Verifier verifier, final ClassStructure originalClassStructure, final InstrumentationContext context, final InstrumentationPackage instrumentationPackage, final OptimizedClassMatcher.Match match) {
        super(327680, cv);
        this.originalMethods = new HashSet<Method>();
        this.existingFields = (Map<String, FieldNode>)Maps.newHashMap();
        this.firstField = true;
        this.verifier = verifier;
        this.className = className;
        this.mixinClassVisitor = mixinClassVisitor;
        this.methods = (Map<Method, MergeMethodVisitor>)Maps.newHashMap((Map<?, ?>)mixinClassVisitor.getMethods());
        final Map<Method, TraceDetails> tracedMethods = (Map<Method, TraceDetails>)Maps.newHashMap((Map<?, ?>)mixinClassVisitor.getWeaveClassInfo().getTracedMethods());
        for (final Map.Entry<Method, Method> entry : context.getBridgeMethods().entrySet()) {
            final MergeMethodVisitor mv = this.methods.remove(entry.getKey());
            if (mv != null) {
                this.methods.put(entry.getValue(), mv);
                final TraceDetails traceDetails = tracedMethods.remove(entry.getKey());
                if (traceDetails == null) {
                    continue;
                }
                tracedMethods.put(entry.getValue(), traceDetails);
            }
        }
        this.context = context;
        this.instrumentationPackage = instrumentationPackage;
        this.newFields = mixinClassVisitor.getWeaveClassInfo().getNewFields();
        this.newMethods = (Map<Method, MergeMethodVisitor>)Maps.newHashMap((Map<?, ?>)mixinClassVisitor.getMethods());
        for (final Method method : originalClassStructure.getMethods()) {
            this.newMethods.remove(method);
        }
        final Set<Method> toRemove = (Set<Method>)Sets.newHashSet();
        for (final Method newMethod : this.newMethods.keySet()) {
            final TraceDetails traceDetails = tracedMethods.get(newMethod);
            if (traceDetails != null) {
                final Level level = MatchType.ExactClass.equals((Object)mixinClassVisitor.getMatchType()) ? Level.FINE : Level.FINER;
                instrumentationPackage.getLogger().log(level, newMethod + " is marked with a Trace annotation, but it does not exist on " + className + ".");
            }
        }
        this.newMethods.keySet().removeAll(toRemove);
        context.addTracedMethods(tracedMethods);
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(this.version = version, access, name, signature, superName, interfaces);
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        if (this.firstField) {
            this.firstField = false;
            for (final InnerClassNode innerClass : this.mixinClassVisitor.getInnerClasses()) {
                if (this.mixinClassVisitor.isAbstractMatch() && !this.instrumentationPackage.getWeaveClasses().containsKey(innerClass.name) && this.instrumentationPackage.getClassBytes().keySet().contains(innerClass.name)) {
                    throw new IllegalInstructionException("Inner classes are not currently supported for abstract merged classes.  " + this.className + " : " + innerClass.name);
                }
                if (this.instrumentationPackage.isWeaved(innerClass.name) || !this.instrumentationPackage.matches(innerClass.name)) {
                    continue;
                }
                this.visitInnerClass(innerClass.name, innerClass.outerName, innerClass.innerName, innerClass.access);
            }
        }
        final FieldNode node = new FieldNode(access, name, desc, signature, value);
        this.existingFields.put(node.name, node);
        return super.visitField(access, name, desc, signature, value);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final Method method = new Method(name, desc);
        this.originalMethods.add(method);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ((access & 0x400) != 0x0) {
            this.methods.remove(method);
            return mv;
        }
        MergeMethodVisitor replayMethodVisitor = this.methods.get(method);
        if ("<init>".equals(name)) {
            if (!"()V".equals(desc) && replayMethodVisitor == null) {
                replayMethodVisitor = this.methods.get(new Method(name, "()V"));
            }
            if (replayMethodVisitor != null) {
                mv = new ConstructorMerger(access, mv, name, desc, signature, exceptions, replayMethodVisitor);
            }
            mv = this.mixinClassVisitor.getWeaveClassInfo().getConstructorMethodVisitor(mv, this.className, access, name, desc);
            replayMethodVisitor = null;
        }
        if (null != replayMethodVisitor) {
            this.methods.remove(method);
            if (replayMethodVisitor.isNewMethod()) {
                throw new IllegalInstructionException("Weaved method " + this.className + '.' + name + desc + " does not call the original method implementation");
            }
            this.instrumentationPackage.getLogger().fine("Injecting code into " + this.className + '.' + method);
            mv = new MethodMerger(access, mv, replayMethodVisitor, name, desc, signature, exceptions);
            this.context.addWeavedMethod(method, this.instrumentationPackage.getImplementationTitle());
        }
        mv = new JSRInlinerAdapter(mv, access, name, desc, signature, exceptions);
        return mv;
    }
    
    static {
        NO_OP_REMAPPER = new Remapper() {};
    }
    
    private class ConstructorMerger extends MethodNode
    {
        private final MethodVisitor writer;
        private final MethodNode newCode;
        private final Method method;
        
        public ConstructorMerger(final int access, final MethodVisitor mv, final String name, final String desc, final String signature, final String[] exceptions, final MethodNode additionalCode) {
            super(327680, access, name, desc, signature, exceptions);
            this.method = new Method(name, desc);
            this.newCode = new MethodNode(327680, additionalCode.access, additionalCode.name, additionalCode.desc, additionalCode.signature, additionalCode.exceptions.toArray(new String[0]));
            MethodVisitor codeVisitor = this.newCode;
            codeVisitor = ClassWeaver.this.mixinClassVisitor.getWeaveClassInfo().getMethodVisitor(ClassWeaver.this.className, codeVisitor, access, new Method(name, desc));
            additionalCode.accept(codeVisitor);
            this.writer = mv;
        }
        
        public void visitInsn(final int opcode) {
            if (177 == opcode) {
                final GeneratorAdapter adapter = new GeneratorAdapter(this.access, this.method, this);
                final Label start = adapter.newLabel();
                final Label end = adapter.newLabel();
                final Label handler = adapter.newLabel();
                adapter.visitLabel(start);
                adapter.visitVarInsn(25, 0);
                adapter.loadArgs();
                adapter.visitMethodInsn(182, "____INLINE_ME____", this.newCode.name, this.method.getDescriptor(), false);
                adapter.goTo(end);
                adapter.visitLabel(handler);
                if (Agent.isDebugEnabled()) {
                    adapter.invokeVirtual(Type.getType(Throwable.class), new Method("printStackTrace", "()V"));
                }
                else {
                    adapter.pop();
                }
                adapter.visitLabel(end);
                adapter.visitTryCatchBlock(start, end, handler, Type.getType(Throwable.class).getInternalName());
            }
            super.visitInsn(opcode);
        }
        
        public void visitEnd() {
            this.instructions.resetLabels();
            MethodVisitor mv = this.writer;
            mv = new MethodCallInlinerAdapter(ClassWeaver.this.className, this.access, this.name, this.desc, mv, false) {
                protected InlinedMethod mustInline(final String owner, final String name, final String desc) {
                    if ("____INLINE_ME____".equals(owner)) {
                        ClassWeaver.this.instrumentationPackage.getLogger().finer("Inline constructor " + name);
                        return new InlinedMethod(ConstructorMerger.this.newCode, ClassWeaver.NO_OP_REMAPPER);
                    }
                    return null;
                }
            };
            this.accept(mv);
        }
    }
    
    private class MethodMerger extends MethodNode
    {
        private final MergeMethodVisitor codeToInject;
        private final MethodVisitor writer;
        private final String name;
        private final String desc;
        private final int access;
        private final Method method;
        
        public MethodMerger(final int access, final MethodVisitor mv, final MergeMethodVisitor codeToInject, final String name, final String desc, final String signature, final String[] exceptions) {
            super(327680, access, name, desc, signature, exceptions);
            this.writer = mv;
            this.codeToInject = codeToInject;
            this.method = new Method(name, desc);
            this.name = name;
            this.desc = desc;
            this.access = access;
        }
        
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            return this.writer.visitAnnotation(desc, visible);
        }
        
        public AnnotationVisitor visitAnnotationDefault() {
            return this.writer.visitAnnotationDefault();
        }
        
        public void visitAttribute(final Attribute attr) {
            this.writer.visitAttribute(attr);
        }
        
        public void visitEnd() {
            final Map<Method, MethodNode> methodsToInline = (Map<Method, MethodNode>)Maps.newHashMap((Map<?, ?>)ClassWeaver.this.mixinClassVisitor.getMethodsToInline());
            this.instructions.resetLabels();
            this.codeToInject.instructions.resetLabels();
            for (final MethodNode newCode : methodsToInline.values()) {
                newCode.instructions.resetLabels();
            }
            MethodVisitor mv = new MethodCallInlinerAdapter(ClassWeaver.this.className, this.access, this.name, this.desc, this.writer, false) {
                protected InlinedMethod mustInline(final String owner, final String name, final String desc) {
                    if (owner.equals(MethodMerger.this.codeToInject.getClassName()) && new Method(name, desc).equals(MethodMerger.this.codeToInject.getMethod())) {
                        ClassWeaver.this.instrumentationPackage.getLogger().finer("Inline original implementation of " + name + desc);
                        return new InlinedMethod(MethodMerger.this, ClassWeaver.NO_OP_REMAPPER);
                    }
                    return null;
                }
            };
            mv = ClassWeaver.this.mixinClassVisitor.getWeaveClassInfo().getMethodVisitor(ClassWeaver.this.className, mv, this.access, this.method);
            mv = new MethodCallInlinerAdapter(ClassWeaver.this.className, this.access, this.name, this.desc, mv, false) {
                protected InlinedMethod mustInline(final String owner, final String name, final String desc) {
                    final MethodNode methodToInline = methodsToInline.get(new Method(name, desc));
                    if (owner.equals(MethodMerger.this.codeToInject.getClassName()) && methodToInline != null) {
                        ClassWeaver.this.instrumentationPackage.getLogger().finer("Inlining " + name + desc);
                        return new InlinedMethod(methodToInline, ClassWeaver.NO_OP_REMAPPER);
                    }
                    return null;
                }
            };
            if (ClassWeaver.this.version < 49) {
                mv = new FixLoadClassMethodAdapter(this.access, this.method, mv);
            }
            this.codeToInject.accept(mv);
        }
        
        public void visitMaxs(final int maxStack, final int maxLocals) {
            this.writer.visitMaxs(0, 0);
        }
    }
}

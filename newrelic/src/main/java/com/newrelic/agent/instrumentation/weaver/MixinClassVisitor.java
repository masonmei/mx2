// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.org.objectweb.asm.util.TraceClassVisitor;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.AbstractInsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InsnNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.LineNumberNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.LabelNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.FrameNode;
import java.util.Iterator;
import java.util.Collection;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import com.newrelic.agent.deps.org.objectweb.asm.tree.MethodNode;
import com.newrelic.agent.deps.org.objectweb.asm.tree.InnerClassNode;
import java.util.List;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

class MixinClassVisitor extends ClassVisitor
{
    private final Map<Method, MergeMethodVisitor> methods;
    String className;
    String[] interfaces;
    private final byte[] bytes;
    private final List<InnerClassNode> innerClasses;
    private final Map<Method, MethodNode> methodsToInline;
    private final InstrumentationPackage instrumentationPackage;
    private final WeavedClassInfo weavedClassInfo;
    
    public MixinClassVisitor(final byte[] bytes, final InstrumentationPackage instrumentationPackage, final WeavedClassInfo weavedClassInfo) {
        super(327680);
        this.methods = (Map<Method, MergeMethodVisitor>)Maps.newHashMap();
        this.innerClasses = (List<InnerClassNode>)Lists.newArrayList();
        this.methodsToInline = (Map<Method, MethodNode>)Maps.newHashMap();
        this.bytes = bytes;
        this.instrumentationPackage = instrumentationPackage;
        this.weavedClassInfo = weavedClassInfo;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name;
        this.interfaces = interfaces;
    }
    
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        this.innerClasses.add(new InnerClassNode(name, outerName, innerName, access));
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final Method method = new Method(name, desc);
        if ((0x440 & access) != 0x0) {
            return null;
        }
        if ("<clinit>".equals(name)) {
            this.instrumentationPackage.getLogger().warning(this.className + " in " + this.instrumentationPackage.implementationTitle + " contains a class constructor (static initializer).  This code will be discarded.");
            return null;
        }
        final MergeMethodVisitor node = new MergeMethodVisitor(this.instrumentationPackage, this.className, 327680, access, name, desc, signature, exceptions);
        this.methods.put(method, node);
        return node;
    }
    
    public void visitEnd() {
        super.visitEnd();
        this.removeInitSuperCalls();
        final List<Method> emptyMethods = (List<Method>)Lists.newArrayList();
        for (final Map.Entry<Method, MergeMethodVisitor> entry : this.methods.entrySet()) {
            if (entry.getValue().instructions.size() == 0) {
                emptyMethods.add(entry.getKey());
            }
            else {
                if (!entry.getValue().isNewMethod()) {
                    continue;
                }
                this.methodsToInline.put(entry.getKey(), entry.getValue());
            }
        }
        this.methods.keySet().removeAll(emptyMethods);
    }
    
    private void removeInitSuperCalls() {
        for (final MergeMethodVisitor methodNode : this.methods.values()) {
            if (MergeMethodVisitor.isInitMethod(methodNode.name)) {
                this.removeInitSuperCalls(methodNode);
                this.removeEmptyInitMethod(methodNode);
            }
        }
    }
    
    private void removeEmptyInitMethod(final MergeMethodVisitor methodNode) {
        final AbstractInsnNode[] arr$;
        final AbstractInsnNode[] insnNodes = arr$ = methodNode.instructions.toArray();
        for (final AbstractInsnNode node : arr$) {
            if (!(node instanceof FrameNode) && !(node instanceof LabelNode)) {
                if (!(node instanceof LineNumberNode)) {
                    if (!(node instanceof InsnNode) || node.getOpcode() != 177) {
                        this.instrumentationPackage.getLogger().finest("Keeping <init> method after encountering opcode " + node.getOpcode());
                        return;
                    }
                }
            }
        }
        this.instrumentationPackage.getLogger().finest("Discarding <init> method on weaved class " + this.className);
        methodNode.instructions.clear();
    }
    
    private void removeInitSuperCalls(final MergeMethodVisitor methodNode) {
        final AbstractInsnNode[] arr$;
        final AbstractInsnNode[] insnNodes = arr$ = methodNode.instructions.toArray();
        for (final AbstractInsnNode insn : arr$) {
            methodNode.instructions.remove(insn);
            if (insn.getOpcode() == 183) {
                final MethodInsnNode methodInsnNode = (MethodInsnNode)insn;
                if (methodInsnNode.owner.equals(this.weavedClassInfo.getSuperName())) {
                    return;
                }
            }
        }
        throw new IllegalInstructionException("Error processing " + this.className + '.' + methodNode.name + methodNode.desc);
    }
    
    public boolean isAbstractMatch() {
        return this.getMatchType() != null && !this.getMatchType().isExactMatch();
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public MatchType getMatchType() {
        return this.weavedClassInfo.getMatchType();
    }
    
    public List<InnerClassNode> getInnerClasses() {
        return this.innerClasses;
    }
    
    public Map<Method, MergeMethodVisitor> getMethods() {
        return Collections.unmodifiableMap((Map<? extends Method, ? extends MergeMethodVisitor>)this.methods);
    }
    
    public Map<Method, MethodNode> getMethodsToInline() {
        return this.methodsToInline;
    }
    
    public WeavedClassInfo getWeaveClassInfo() {
        return this.weavedClassInfo;
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.bytes);
        result = 31 * result + ((this.className == null) ? 0 : this.className.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MixinClassVisitor other = (MixinClassVisitor)obj;
        if (this.className == null) {
            if (other.className != null) {
                return false;
            }
        }
        else if (!this.className.equals(other.className)) {
            return false;
        }
        return Arrays.equals(this.bytes, other.bytes);
    }
    
    public void print() {
        final TraceClassVisitor cv = new TraceClassVisitor(new PrintWriter(System.err));
        for (final MergeMethodVisitor m : this.methods.values()) {
            m.instructions.accept(cv.visitMethod(m.access, m.name, m.desc, m.signature, null));
        }
        cv.visitEnd();
    }
}

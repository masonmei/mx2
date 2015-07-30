// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.agent.deps.com.google.common.collect.Sets;
import java.util.Iterator;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.FieldVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.agent.logging.IAgentLogger;
import java.util.Set;
import java.util.Map;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

class ReferencesVisitor extends ClassVisitor
{
    private String className;
    private final Map<String, Set<MethodWithAccess>> referencedClassMethods;
    private final Map<String, Set<MethodWithAccess>> referencedInterfaceMethods;
    private final WeavedClassInfo weaveDetails;
    private final IAgentLogger logger;
    
    public ReferencesVisitor(final IAgentLogger logger, final WeavedClassInfo weaveDetails, final ClassVisitor classVisitor, final Map<String, Set<MethodWithAccess>> referencedClasses, final Map<String, Set<MethodWithAccess>> referencedInterfaces) {
        super(327680, classVisitor);
        this.weaveDetails = weaveDetails;
        this.referencedClassMethods = referencedClasses;
        this.referencedInterfaceMethods = referencedInterfaces;
        this.logger = logger;
    }
    
    public MatchType getMatchType() {
        return (this.weaveDetails == null) ? null : this.weaveDetails.getMatchType();
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name;
        this.addClassReference(Type.getObjectType(name), null);
        if (null != superName) {
            this.addClassReference(Type.getObjectType(superName), null);
        }
        for (final String interfaceName : interfaces) {
            this.addInterfaceReference(Type.getObjectType(interfaceName), null);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }
    
    public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
        this.addClassReference(Type.getType(desc), null);
        return super.visitField(access, name, desc, signature, value);
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        final Method method = new Method(name, desc);
        final MethodWithAccess methodWithAccess = new MethodWithAccess((access & 0x8) == 0x8, method);
        this.addClassReference(method.getReturnType(), null);
        for (final Type argType : method.getArgumentTypes()) {
            this.addClassReference(argType, null);
        }
        final boolean isAbstract = (0x400 & access) != 0x0;
        if (this.weaveDetails != null && (this.weaveDetails.getWeavedMethods().contains(method) || isAbstract)) {
            if (MatchType.Interface.equals((Object)this.getMatchType())) {
                this.addInterfaceReference(Type.getObjectType(this.className), methodWithAccess);
            }
            else {
                this.addClassReference(Type.getObjectType(this.className), methodWithAccess);
            }
        }
        if (isAbstract) {
            return mv;
        }
        final boolean synthetic = (0x1000 & access) != 0x0;
        mv = new MethodVisitor(327680, mv) {
            public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                if (synthetic && null != ReferencesVisitor.this.weaveDetails) {
                    ReferencesVisitor.this.logger.warning(ReferencesVisitor.this.className + " references a synthetic method to access " + owner + "." + name + desc + ".  This will not work correctly in instrumented classes.");
                }
                if (!ReferencesVisitor.this.className.equals(owner) && ((opcode & 0xB7) == 0x0 || !name.equals("<init>") || !desc.equals("()V"))) {
                    final MethodWithAccess method = new MethodWithAccess(184 == opcode, new Method(name, desc));
                    if (185 == opcode) {
                        ReferencesVisitor.this.addInterfaceReference(Type.getObjectType(owner), method);
                    }
                    else {
                        ReferencesVisitor.this.addClassReference(Type.getObjectType(owner), method);
                    }
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
            
            public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        };
        return mv;
    }
    
    public void visitEnd() {
        super.visitEnd();
        for (final Map.Entry<String, Set<MethodWithAccess>> entry : this.referencedInterfaceMethods.entrySet()) {
            final Set<MethodWithAccess> methods = this.referencedClassMethods.remove(entry.getKey());
            if (methods != null && !methods.isEmpty()) {
                throw new InvalidReferenceException(entry.getKey() + " is referenced as a class when invoking methods " + methods + ", but as an interface when invoking methods " + entry.getValue());
            }
        }
    }
    
    private void addClassReference(final Type type, final MethodWithAccess method) {
        addReference(type, method, this.referencedClassMethods);
    }
    
    private void addInterfaceReference(final Type type, final MethodWithAccess method) {
        addReference(type, method, this.referencedInterfaceMethods);
    }
    
    private static void addReference(final Type type, final MethodWithAccess method, final Map<String, Set<MethodWithAccess>> references) {
        if (type.getSort() == 10) {
            final String internalName = type.getInternalName();
            if (internalName != null) {
                Set<MethodWithAccess> referencedMethods = references.get(internalName);
                if (referencedMethods == null) {
                    referencedMethods = (Set<MethodWithAccess>)Sets.newHashSet();
                    references.put(internalName, referencedMethods);
                }
                if (method != null) {
                    referencedMethods.add(method);
                }
            }
            return;
        }
        if (type.getSort() == 9) {
            addReference(type.getElementType(), method, references);
        }
    }
}

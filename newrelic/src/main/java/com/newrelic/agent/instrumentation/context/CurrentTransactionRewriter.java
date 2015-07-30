// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.util.asm.BytecodeGenProxyBuilder;
import com.newrelic.agent.bridge.Instrumentation;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.agent.deps.com.google.common.collect.Sets;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.bridge.AsyncApi;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import java.util.Set;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class CurrentTransactionRewriter
{
    public static ClassVisitor rewriteCurrentTransactionReferences(final ClassVisitor cv, final ClassReader reader) {
        final Set<Method> localTransactionMethods = getLocalTransactionMethods(reader);
        if (localTransactionMethods.isEmpty()) {
            return cv;
        }
        return new ClassVisitor(327680, cv) {
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (localTransactionMethods.contains(new Method(name, desc))) {
                    mv = new RewriteVisitor(mv, access, name, desc);
                }
                return mv;
            }
        };
    }
    
    private static boolean isCurrentTransactionReference(final int opcode, final String owner, final String name) {
        return 178 == opcode && BridgeUtils.isTransactionType(owner) && "CURRENT".equals(name);
    }
    
    private static boolean isCurrentTransactionMethod(final String owner, final String name) {
        return BridgeUtils.isAgentType(owner) && "getTransaction".equals(name);
    }
    
    private static boolean isAsyncApiMethod(final String owner, final String name) {
        return Type.getInternalName(AsyncApi.class).equals(owner) && "resumeAsync".equals(name);
    }
    
    private static Set<Method> getLocalTransactionMethods(final ClassReader reader) {
        final Set<Method> methods = (Set<Method>)Sets.newHashSet();
        final ClassVisitor cv = new ClassVisitor(327680) {
            public MethodVisitor visitMethod(final int access, final String methodName, final String methodDesc, final String signature, final String[] exceptions) {
                return new MethodVisitor(327680) {
                    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
                        if (isCurrentTransactionReference(opcode, owner, name)) {
                            methods.add(new Method(methodName, methodDesc));
                        }
                    }
                    
                    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        if (isAsyncApiMethod(owner, name) || isCurrentTransactionMethod(owner, name)) {
                            methods.add(new Method(methodName, methodDesc));
                        }
                    }
                };
            }
        };
        reader.accept(cv, 6);
        return methods;
    }
    
    private static class RewriteVisitor extends AdviceAdapter
    {
        final int transactionLocal;
        
        protected RewriteVisitor(final MethodVisitor mv, final int access, final String name, final String desc) {
            super(327680, mv, access, name, desc);
            this.transactionLocal = this.newLocal(BridgeUtils.TRANSACTION_TYPE);
        }
        
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            if (isCurrentTransactionReference(opcode, owner, name)) {
                this.loadLocal(this.transactionLocal);
            }
            else {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }
        
        protected void onMethodEnter() {
            super.onMethodEnter();
            this.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "instrumentation", BridgeUtils.INSTRUMENTATION_TYPE);
            BytecodeGenProxyBuilder.newBuilder(Instrumentation.class, this, false).build().getTransaction();
            this.storeLocal(this.transactionLocal, BridgeUtils.TRANSACTION_TYPE);
        }
        
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
            if (isCurrentTransactionMethod(owner, name)) {
                this.pop();
                this.loadLocal(this.transactionLocal);
            }
            else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (isAsyncApiMethod(owner, name)) {
                    this.dup();
                    this.storeLocal(this.transactionLocal);
                }
            }
        }
    }
}

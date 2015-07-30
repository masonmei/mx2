// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import java.util.logging.Level;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class LogWeavedMethodInvocationsVisitor extends ClassVisitor
{
    private final InstrumentationPackage instrumentationPackage;
    private String className;
    
    public LogWeavedMethodInvocationsVisitor(final InstrumentationPackage instrumentationPackage, final ClassVisitor cv) {
        super(327680, cv);
        this.instrumentationPackage = instrumentationPackage;
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ((access & 0x400) != 0x0) {
            return mv;
        }
        if ("<init>".equals(name)) {
            return new BaseLogAdapter(327680, mv, access, name, desc) {
                protected void onMethodExit(final int opcode) {
                    this.logMethod();
                }
            };
        }
        return new BaseLogAdapter(327680, mv, access, name, desc) {
            protected void onMethodEnter() {
                this.logMethod();
            }
        };
    }
    
    private class BaseLogAdapter extends AdviceAdapter
    {
        private final Method method;
        
        protected BaseLogAdapter(final int api, final MethodVisitor mv, final int access, final String name, final String desc) {
            super(api, mv, access, name, desc);
            this.method = new Method(name, desc);
        }
        
        protected void logMethod() {
            final String message = Strings.join(LogWeavedMethodInvocationsVisitor.this.className, ".", this.method.toString(), " invoked");
            BridgeUtils.getLogger(this).logToChild(LogWeavedMethodInvocationsVisitor.this.instrumentationPackage.getImplementationTitle(), Level.FINEST, message, (Object[])null);
        }
    }
}

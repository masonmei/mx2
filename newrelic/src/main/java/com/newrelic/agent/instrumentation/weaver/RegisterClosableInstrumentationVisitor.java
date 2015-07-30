// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import java.io.Closeable;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.util.asm.BytecodeGenProxyBuilder;
import com.newrelic.agent.bridge.Instrumentation;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class RegisterClosableInstrumentationVisitor extends ClassVisitor
{
    private final InstrumentationPackage instrumentationPackage;
    
    public RegisterClosableInstrumentationVisitor(final InstrumentationPackage instrumentationPackage, final ClassVisitor cv) {
        super(327680, cv);
        this.instrumentationPackage = instrumentationPackage;
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new AdviceAdapter(327680, mv, access, name, desc) {
            public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                if (BridgeUtils.PRIVATE_API_TYPE.getInternalName().equals(owner) && "addSampler".equals(name)) {
                    this.dup();
                    this.getStatic(BridgeUtils.AGENT_BRIDGE_TYPE, "instrumentation", BridgeUtils.INSTRUMENTATION_TYPE);
                    this.swap();
                    this.push(RegisterClosableInstrumentationVisitor.this.instrumentationPackage.implementationTitle);
                    this.swap();
                    final BytecodeGenProxyBuilder<Instrumentation> builder = BytecodeGenProxyBuilder.newBuilder(Instrumentation.class, this, false);
                    builder.build().registerCloseable("ImplementationTitle", (Closeable)null);
                }
            }
        };
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.weaver;

import com.newrelic.api.agent.Logger;
import java.util.logging.Level;
import com.newrelic.agent.deps.org.objectweb.asm.commons.GeneratorAdapter;
import com.newrelic.agent.instrumentation.tracing.BridgeUtils;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.deps.org.objectweb.asm.commons.AdviceAdapter;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;

public class LogApiCallsVisitor extends ClassVisitor
{
    private final InstrumentationPackage instrumentationPackage;
    private final Type[] apiClassesTypes;
    private String className;
    
    public LogApiCallsVisitor(final InstrumentationPackage instrumentationPackage, final ClassVisitor cv) {
        super(327680, cv);
        this.instrumentationPackage = instrumentationPackage;
        this.apiClassesTypes = new Type[AgentBridge.API_CLASSES.length];
        for (int i = 0; i < AgentBridge.API_CLASSES.length; ++i) {
            this.apiClassesTypes[i] = Type.getType(AgentBridge.API_CLASSES[i]);
        }
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }
    
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        final Method method = new Method(name, desc);
        return new AdviceAdapter(327680, mv, access, name, desc) {
            public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                this.logApiCall(method, access, owner, name, desc, LogApiCallsVisitor.this.apiClassesTypes);
            }
            
            private boolean logApiCall(final Method instrumentationMethod, final int access, final String owner, final String name, final String desc, final Type... apiTypes) {
                for (final Type apiType : apiTypes) {
                    if (apiType.getInternalName().equals(owner)) {
                        final String logMessage = Strings.join(LogApiCallsVisitor.this.className, ".", instrumentationMethod.toString(), " called ", apiType.getClassName(), ".", new Method(name, desc).toString());
                        final Logger logger = BridgeUtils.getLogger(this);
                        logger.logToChild(LogApiCallsVisitor.this.instrumentationPackage.getImplementationTitle(), Level.FINEST, logMessage, (Object[])null);
                        return true;
                    }
                }
                return false;
            }
        };
    }
}

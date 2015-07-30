// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class ReflectionStyleClassMethodAdapter extends AbstractTracingMethodAdapter
{
    private final int tracerFactoryId;
    
    public ReflectionStyleClassMethodAdapter(final GenericClassAdapter genericClassAdapter, final MethodVisitor mv, final int access, final Method method, final int tracerFactoryId) {
        super(genericClassAdapter, mv, access, method);
        this.tracerFactoryId = tracerFactoryId;
        if (Agent.LOG.isFinestEnabled()) {
            Agent.LOG.finest("Using fallback instrumentation on " + genericClassAdapter.className + "/" + this.methodName + this.methodDesc);
        }
    }
    
    protected void loadGetTracerArguments() {
        this.methodBuilder.loadInvocationHandlerFromProxy();
        this.methodBuilder.loadInvocationHandlerProxyAndMethod(this.tracerFactoryId);
        this.methodBuilder.loadArray(Object.class, this.genericClassAdapter.className, this.methodName, this.methodDesc, MethodBuilder.LOAD_THIS, MethodBuilder.LOAD_ARG_ARRAY);
    }
}

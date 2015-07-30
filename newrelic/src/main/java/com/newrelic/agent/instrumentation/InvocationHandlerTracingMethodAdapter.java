// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.deps.org.objectweb.asm.Type;
import com.newrelic.agent.deps.org.objectweb.asm.commons.Method;
import com.newrelic.agent.deps.org.objectweb.asm.MethodVisitor;

public class InvocationHandlerTracingMethodAdapter extends AbstractTracingMethodAdapter
{
    public InvocationHandlerTracingMethodAdapter(final GenericClassAdapter genericClassAdapter, final MethodVisitor mv, final int access, final Method method) {
        super(genericClassAdapter, mv, access, method);
    }
    
    protected void loadGetTracerArguments() {
        this.getStatic(Type.getObjectType(this.genericClassAdapter.className), "__nr__InvocationHandlers", MethodBuilder.INVOCATION_HANDLER_ARRAY_TYPE);
        this.push(this.getInvocationHandlerIndex());
        this.arrayLoad(this.getTracerType());
        this.methodBuilder.loadInvocationHandlerProxyAndMethod(null).loadArray(Object.class, MethodBuilder.LOAD_THIS, MethodBuilder.LOAD_ARG_ARRAY);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

class NoOpInvocationHandler implements InvocationHandler
{
    static final InvocationHandler INVOCATION_HANDLER;
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return null;
    }
    
    static {
        INVOCATION_HANDLER = new NoOpInvocationHandler();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import java.lang.reflect.InvocationHandler;

public interface ExitTracer extends InvocationHandler, TracedMethod
{
    void finish(int p0, Object p1);
    
    void finish(Throwable p0);
}

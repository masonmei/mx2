// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

public interface EntryInvocationHandler extends PointCutInvocationHandler
{
    void handleInvocation(ClassMethodSignature p0, Object p1, Object[] p2);
}

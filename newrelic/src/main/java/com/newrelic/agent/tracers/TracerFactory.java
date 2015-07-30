// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.Transaction;

public interface TracerFactory extends PointCutInvocationHandler
{
    Tracer getTracer(Transaction p0, ClassMethodSignature p1, Object p2, Object[] p3);
}

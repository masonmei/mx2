// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Transaction;

public abstract class AbstractTracerFactory implements TracerFactory
{
    public Tracer getTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        return this.canCreateTracer() ? this.doGetTracer(transaction, sig, object, args) : null;
    }
    
    public abstract Tracer doGetTracer(final Transaction p0, final ClassMethodSignature p1, final Object p2, final Object[] p3);
    
    public boolean canCreateTracer() {
        return !ServiceFactory.getServiceManager().getCircuitBreakerService().isTripped();
    }
}

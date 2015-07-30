// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.tracers.TracerFactory;

public abstract class TracerFactoryPointCut extends PointCut implements TracerFactory
{
    public TracerFactoryPointCut(final Class<? extends TracerFactoryPointCut> pointCutClass, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(new PointCutConfiguration(pointCutClass), classMatcher, methodMatcher);
    }
    
    public TracerFactoryPointCut(final PointCutConfiguration config, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(config, classMatcher, methodMatcher);
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public Tracer getTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        return this.canCreateTracer() ? this.doGetTracer(transaction, sig, object, args) : null;
    }
    
    protected abstract Tracer doGetTracer(final Transaction p0, final ClassMethodSignature p1, final Object p2, final Object[] p3);
    
    public boolean canCreateTracer() {
        return !ServiceFactory.getServiceManager().getCircuitBreakerService().isTripped();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.tracers.TracerFactory;

public class DefaultPointCut extends PointCut
{
    private final TracerFactory tracerFactory;
    
    public DefaultPointCut(final PointCutConfiguration config, final TracerFactory tracerFactory, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(config, classMatcher, methodMatcher);
        this.tracerFactory = tracerFactory;
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this.tracerFactory;
    }
    
    public String toString() {
        return "DefaultPointCut:" + this.getClass().getName();
    }
}

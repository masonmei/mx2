// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public abstract class ExternalComponentPointCut extends TracerFactoryPointCut
{
    public ExternalComponentPointCut(final PointCutConfiguration config, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(config, classMatcher, methodMatcher);
    }
    
    public final Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Tracer parent = transaction.getTransactionActivity().getLastTracer();
        if (parent != null && parent instanceof IgnoreChildSocketCalls) {
            return null;
        }
        return this.getExternalTracer(transaction, sig, object, args);
    }
    
    protected abstract Tracer getExternalTracer(final Transaction p0, final ClassMethodSignature p1, final Object p2, final Object[] p3);
}

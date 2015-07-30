// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class MathCSConcurrentPointCut extends TracerFactoryPointCut
{
    public MathCSConcurrentPointCut(final ClassTransformer classTransformer) {
        super(MathCSConcurrentPointCut.class, new InterfaceMatcher("edu/emory/mathcs/backport/java/util/concurrent/Callable"), PointCut.createExactMethodMatcher("call", "()Ljava/lang/Object;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object callable, final Object[] args) {
        return new OtherRootTracer(transaction, sig, callable, new SimpleMetricNameFormat("OtherTransaction/Job/emoryConcurrentCallable"));
    }
    
    protected boolean isDispatcher() {
        return true;
    }
}

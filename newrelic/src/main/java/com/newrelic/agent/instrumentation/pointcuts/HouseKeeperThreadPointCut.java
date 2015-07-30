// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class HouseKeeperThreadPointCut extends TracerFactoryPointCut
{
    public HouseKeeperThreadPointCut(final ClassTransformer classTransformer) {
        super(HouseKeeperThreadPointCut.class, new ExactClassMatcher("org/logicalcobwebs/proxool/HouseKeeperThread"), PointCut.createExactMethodMatcher("run", "()V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object thread, final Object[] args) {
        return new OtherRootTracer(transaction, sig, thread, new ClassMethodMetricNameFormat(sig, thread, "OtherTransaction/Job"));
    }
    
    protected boolean isDispatcher() {
        return true;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class QuartzSystemPointCut extends TracerFactoryPointCut
{
    public QuartzSystemPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("quartz_system"), ExactClassMatcher.or("org/quartz/impl/jdbcjobstore/JobStoreSupport", "org/quartz/simpl/RAMJobStore"), PointCut.createExactMethodMatcher("acquireNextTrigger", "(Lorg/quartz/core/SchedulingContext;)V", "(Lorg/quartz/core/SchedulingContext;J)Lorg/quartz/Trigger;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object job, final Object[] args) {
        return new QuartzJobTracer(transaction, sig, job);
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    private static class QuartzJobTracer extends OtherRootTracer
    {
        public QuartzJobTracer(final Transaction transaction, final ClassMethodSignature sig, final Object job) {
            super(transaction, sig, job, new ClassMethodMetricNameFormat(sig, job, "OtherTransaction/Job"));
            transaction.setIgnore(true);
        }
    }
}

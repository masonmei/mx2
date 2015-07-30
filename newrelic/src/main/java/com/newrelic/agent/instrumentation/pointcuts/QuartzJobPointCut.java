// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class QuartzJobPointCut extends TracerFactoryPointCut
{
    public QuartzJobPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("quartz_job"), new InterfaceMatcher("org/quartz/Job"), PointCut.createExactMethodMatcher("execute", "(Lorg/quartz/JobExecutionContext;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object job, final Object[] args) {
        return new QuartzJobTracer(transaction, sig, job, args[0]);
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    private static class QuartzJobTracer extends OtherRootTracer
    {
        public QuartzJobTracer(final Transaction transaction, final ClassMethodSignature sig, final Object job, final Object context) {
            super(transaction, sig, job, new ClassMethodMetricNameFormat(sig, job, "OtherTransaction/Job"));
            try {
                final Object jobDetail = context.getClass().getMethod("getJobDetail", (Class<?>[])new Class[0]).invoke(context, new Object[0]);
                this.setAttribute("name", jobDetail.getClass().getMethod("getFullName", (Class<?>[])new Class[0]).invoke(jobDetail, new Object[0]));
            }
            catch (Throwable e) {
                Agent.LOG.finer(MessageFormat.format("An error occurred getting a Quartz job name", e.toString()));
            }
            if (Agent.isDebugEnabled()) {
                Agent.LOG.fine("Quartz job started");
            }
        }
    }
}

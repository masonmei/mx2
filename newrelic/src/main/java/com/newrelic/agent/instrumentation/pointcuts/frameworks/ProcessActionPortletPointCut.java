// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks;

import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;
import com.newrelic.agent.instrumentation.ClassTransformer;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ProcessActionPortletPointCut extends AbstractPortletPointCut
{
    public ProcessActionPortletPointCut(final ClassTransformer classTransformer) {
        super(ProcessActionPortletPointCut.class, PointCut.createExactMethodMatcher("processAction", "(Ljavax/portlet/ActionRequest;Ljavax/portlet/ActionResponse;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object portlet, final Object[] args) {
        return new DefaultTracer(transaction, sig, portlet, new ClassMethodMetricNameFormat(sig, portlet, "Portlet"));
    }
}

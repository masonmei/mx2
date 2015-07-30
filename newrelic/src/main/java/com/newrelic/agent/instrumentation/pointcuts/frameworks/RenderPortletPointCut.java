// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class RenderPortletPointCut extends AbstractPortletPointCut
{
    public RenderPortletPointCut(final ClassTransformer classTransformer) {
        super(RenderPortletPointCut.class, PointCut.createExactMethodMatcher("render", "(Ljavax/portlet/RenderRequest;Ljavax/portlet/RenderResponse;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object portlet, final Object[] args) {
        return new DefaultTracer(transaction, sig, portlet, new ClassMethodMetricNameFormat(sig, portlet, "Portlet"));
    }
}

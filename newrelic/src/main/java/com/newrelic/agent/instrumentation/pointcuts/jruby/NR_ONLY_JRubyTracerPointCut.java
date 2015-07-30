// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.jruby;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class NR_ONLY_JRubyTracerPointCut extends TracerFactoryPointCut
{
    private final int METRIC_NAME_ARGUMENT = 2;
    
    public NR_ONLY_JRubyTracerPointCut(final ClassTransformer ct) {
        super(NR_ONLY_JRubyTracerPointCut.class, new ExactClassMatcher("com/newrelic/api/jruby/JavaAgentBackend"), new ExactMethodMatcher("trace", new String[0]));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        final String metric = args[2].toString();
        return new DefaultTracer(transaction, sig, object, new SimpleMetricNameFormat(metric));
    }
}

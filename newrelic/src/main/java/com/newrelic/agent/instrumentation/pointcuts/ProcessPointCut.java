// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ProcessPointCut extends TracerFactoryPointCut
{
    public static final String UNIXPROCESS_CLASS_NAME = "java/lang/UNIXProcess";
    public static final String PROCESS_IMPL_CLASS_NAME = "java/lang/ProcessImpl";
    
    public ProcessPointCut(final ClassTransformer classTransformer) {
        super(ProcessPointCut.class, ExactClassMatcher.or("java/lang/ProcessImpl", "java/lang/UNIXProcess"), PointCut.createExactMethodMatcher("waitFor", "()I"));
        classTransformer.getClassNameFilter().addIncludeClass("java/lang/ProcessImpl");
        classTransformer.getClassNameFilter().addIncludeClass("java/lang/UNIXProcess");
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        return new DefaultTracer(transaction, sig, object, new ClassMethodMetricNameFormat(sig, object));
    }
}

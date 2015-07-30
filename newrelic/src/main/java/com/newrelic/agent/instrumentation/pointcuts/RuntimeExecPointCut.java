// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts;

import com.newrelic.agent.InstrumentationProxy;
import java.lang.instrument.UnmodifiableClassException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class RuntimeExecPointCut extends TracerFactoryPointCut
{
    public RuntimeExecPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(RuntimeExecPointCut.class.getName(), null, false), new ExactClassMatcher("java/lang/Runtime"), PointCut.createExactMethodMatcher("exec", "(Ljava/lang/String;[Ljava/lang/String;Ljava/io/File;)Ljava/lang/Process;", "([Ljava/lang/String;[Ljava/lang/String;Ljava/io/File;)Ljava/lang/Process;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        return new DefaultTracer(transaction, sig, object, new ClassMethodMetricNameFormat(sig, object));
    }
    
    public void noticeTransformerStarted(final ClassTransformer classTransformer) {
        final InstrumentationProxy instrumentation = ServiceFactory.getAgent().getInstrumentation();
        if (instrumentation.isRetransformClassesSupported()) {
            try {
                instrumentation.retransformClasses(Runtime.class);
            }
            catch (UnmodifiableClassException e) {
                Agent.LOG.log(Level.FINER, "Unable to retransform java.lang.Runtime", e);
            }
        }
    }
}

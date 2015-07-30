// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.jersey;

import java.lang.reflect.Field;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import java.lang.reflect.Method;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class JerseyResourcePointCut extends TracerFactoryPointCut
{
    public JerseyResourcePointCut(final ClassTransformer transformer) {
        super(new PointCutConfiguration(JerseyResourcePointCut.class), ExactClassMatcher.or("com/sun/jersey/server/impl/model/method/dispatch/ResourceJavaMethodDispatcher", "com/sun/jersey/impl/model/method/dispatch/ResourceJavaMethodDispatcher"), PointCut.createExactMethodMatcher("dispatch", "(Ljava/lang/Object;Lcom/sun/jersey/api/core/HttpContext;)V"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object dispatcher, final Object[] args) {
        try {
            final Class<?> dispatcherClass = dispatcher.getClass().getClassLoader().loadClass(sig.getClassName());
            final Field methodField = dispatcherClass.getDeclaredField("method");
            methodField.setAccessible(true);
            final Method method = (Method)methodField.get(dispatcher);
            final String methodName = method.getName();
            final ClassMethodMetricNameFormat metricNameFormatter = new ClassMethodMetricNameFormat(new ClassMethodSignature(args[0].getClass().getName(), methodName, ""), args[0]);
            return new DefaultTracer(transaction, sig, dispatcher, metricNameFormatter);
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINER, "Jersey resource error", e);
            return null;
        }
    }
}

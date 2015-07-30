// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.spring;

import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import java.lang.reflect.Method;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class HandlerMethodInvoker3PointCut extends TracerFactoryPointCut
{
    public HandlerMethodInvoker3PointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("spring_handler_method_invoker"), new ExactClassMatcher("org/springframework/web/bind/annotation/support/HandlerMethodInvoker"), PointCut.createExactMethodMatcher("invokeHandlerMethod", "(Ljava/lang/reflect/Method;Ljava/lang/Object;Lorg/springframework/web/context/request/NativeWebRequest;Lorg/springframework/ui/ExtendedModelMap;)Ljava/lang/Object;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object invoker, final Object[] args) {
        final StringBuilder tracerName = new StringBuilder("Spring/Java");
        final String methodName = ((Method)args[0]).getName();
        final Class<?> controller = args[1].getClass();
        tracerName.append(this.getControllerName(methodName, controller));
        return new DefaultTracer(transaction, sig, invoker, new SimpleMetricNameFormat(tracerName.toString()));
    }
    
    private String getControllerName(final String methodName, final Class<?> controller) {
        String controllerName = controller.getName();
        final int indexOf = controllerName.indexOf("$$EnhancerBy");
        if (indexOf > 0) {
            controllerName = controllerName.substring(0, indexOf);
        }
        return '/' + controllerName + '/' + methodName;
    }
}

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
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class HandlerMethodInvokerPointCut extends MethodInvokerPointCut
{
    private static final String SPRING_2X_METHOD = "doInvokeMethod";
    private static final String SPRING_3X_METHOD = "resolveHandlerArguments";
    
    public HandlerMethodInvokerPointCut(final ClassTransformer classTransformer) {
        super(new ExactClassMatcher("org/springframework/web/bind/annotation/support/HandlerMethodInvoker"), OrMethodMatcher.getMethodMatcher(PointCut.createExactMethodMatcher("doInvokeMethod", "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;"), PointCut.createExactMethodMatcher("resolveHandlerArguments", "(Ljava/lang/reflect/Method;Ljava/lang/Object;Lorg/springframework/web/context/request/NativeWebRequest;Lorg/springframework/ui/ExtendedModelMap;)[Ljava/lang/Object;")));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object invoker, final Object[] args) {
        final String methodName = ((Method)args[0]).getName();
        final Class controller = args[1].getClass();
        if (this.isNormalizeTransactions()) {
            this.setTransactionName(transaction, methodName, controller);
        }
        if ("resolveHandlerArguments".equals(sig.getMethodName())) {
            return null;
        }
        return new DefaultTracer(transaction, sig, invoker, new SimpleMetricNameFormat("Spring/Java/" + controller.getName() + '/' + methodName)) {
            protected void doFinish(final Throwable throwable) {
                if (!HandlerMethodInvokerPointCut.this.isNormalizationDisabled()) {
                    HandlerMethodInvokerPointCut.this.setTransactionName(transaction, methodName, controller);
                }
            }
        };
    }
}

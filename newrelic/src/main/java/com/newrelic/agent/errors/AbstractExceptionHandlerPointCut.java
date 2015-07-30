// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import com.newrelic.agent.TransactionErrorPriority;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

public abstract class AbstractExceptionHandlerPointCut extends TracerFactoryPointCut
{
    public AbstractExceptionHandlerPointCut(final PointCutConfiguration config, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        super(config, classMatcher, methodMatcher);
    }
    
    public final Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object errorHandler, final Object[] args) {
        final Throwable throwable = this.getThrowable(sig, args);
        if (throwable == null) {
            return null;
        }
        return new DefaultTracer(transaction, sig, errorHandler, new ClassMethodMetricNameFormat(sig, errorHandler)) {
            protected void doFinish(final int opcode, final Object returnValue) {
                transaction.setThrowable(throwable, TransactionErrorPriority.API);
                super.doFinish(opcode, returnValue);
            }
        };
    }
    
    protected abstract Throwable getThrowable(final ClassMethodSignature p0, final Object[] p1);
}

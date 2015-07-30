// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.yaml;

import com.newrelic.api.agent.MethodTracer;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.api.agent.MethodTracerFactory;
import com.newrelic.agent.tracers.AbstractTracerFactory;

class CustomTracerFactory extends AbstractTracerFactory
{
    private final MethodTracerFactory tracerFactory;
    
    public CustomTracerFactory(final MethodTracerFactory factory) {
        this.tracerFactory = factory;
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Tracer parent = transaction.getTransactionActivity().getLastTracer();
        final MethodTracer methodTracer = this.tracerFactory.methodInvoked(sig.getMethodName(), object, args);
        if (methodTracer == null) {
            return (parent == null) ? new OtherRootTracer(transaction, sig, object, new ClassMethodMetricNameFormat(sig, object)) : new DefaultTracer(transaction, sig, object);
        }
        if (parent == null) {
            return new OtherRootTracer(transaction, sig, object, new ClassMethodMetricNameFormat(sig, object)) {
                protected void doFinish(final Throwable throwable) {
                    super.doFinish(throwable);
                    methodTracer.methodFinishedWithException(throwable);
                }
                
                protected void doFinish(final int opcode, final Object returnValue) {
                    super.doFinish(opcode, returnValue);
                    methodTracer.methodFinished(returnValue);
                }
            };
        }
        return new DefaultTracer(transaction, sig, object) {
            protected void doFinish(final Throwable throwable) {
                super.doFinish(throwable);
                methodTracer.methodFinishedWithException(throwable);
            }
            
            protected void doFinish(final int opcode, final Object returnValue) {
                super.doFinish(opcode, returnValue);
                methodTracer.methodFinished(returnValue);
            }
        };
    }
}

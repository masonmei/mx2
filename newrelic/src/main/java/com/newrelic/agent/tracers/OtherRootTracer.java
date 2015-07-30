// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.TransactionErrorPriority;
import com.newrelic.agent.dispatchers.OtherDispatcher;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class OtherRootTracer extends DefaultTracer implements TransactionActivityInitiator
{
    private final MetricNameFormat uri;
    
    public OtherRootTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final MetricNameFormat uri) {
        this(transaction.getTransactionActivity(), sig, object, uri);
    }
    
    public OtherRootTracer(final TransactionActivity activity, final ClassMethodSignature sig, final Object object, final MetricNameFormat uri) {
        super(activity, sig, object, new ClassMethodMetricNameFormat(sig, object), 6);
        this.uri = uri;
    }
    
    public Dispatcher createDispatcher() {
        return new OtherDispatcher(this.getTransaction(), this.uri);
    }
    
    protected void doFinish(final Throwable throwable) {
        super.doFinish(throwable);
        if (this.equals(this.getTransaction().getTransactionActivity().getRootTracer())) {
            this.getTransaction().setThrowable(throwable, TransactionErrorPriority.TRACER);
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.dispatchers.AsyncDispatcher;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class AsyncRootTracer extends DefaultTracer implements TransactionActivityInitiator
{
    private final MetricNameFormat uri;
    
    public AsyncRootTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final MetricNameFormat uri) {
        super(transaction, sig, object, uri);
        this.uri = uri;
    }
    
    public Dispatcher createDispatcher() {
        return new AsyncDispatcher(this.getTransaction(), this.uri);
    }
}

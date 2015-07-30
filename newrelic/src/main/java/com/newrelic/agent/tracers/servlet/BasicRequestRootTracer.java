// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.servlet;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.TransactionErrorPriority;
import com.newrelic.agent.dispatchers.WebRequestDispatcher;
import com.newrelic.agent.dispatchers.Dispatcher;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.SkipTracerException;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.api.agent.Response;
import com.newrelic.api.agent.Request;
import com.newrelic.agent.tracers.TransactionActivityInitiator;
import com.newrelic.agent.tracers.DefaultTracer;

public class BasicRequestRootTracer extends DefaultTracer implements TransactionActivityInitiator
{
    private Request request;
    private Response response;
    
    public BasicRequestRootTracer(final Transaction transaction, final ClassMethodSignature sig, final Object dispatcher, final Request request, final Response response) {
        this(transaction, sig, dispatcher, request, response, new SimpleMetricNameFormat("RequestDispatcher", ClassMethodMetricNameFormat.getMetricName(sig, dispatcher, "RequestDispatcher")));
        this.request = request;
        this.response = response;
    }
    
    public BasicRequestRootTracer(final Transaction transaction, final ClassMethodSignature sig, final Object dispatcher, final Request request, final Response response, final MetricNameFormat metricNameFormatter) {
        super(transaction, sig, dispatcher, metricNameFormatter);
        this.request = request;
        this.response = response;
        final Tracer rootTracer = transaction.getTransactionActivity().getRootTracer();
        if (rootTracer != null) {
            throw new SkipTracerException();
        }
    }
    
    public Dispatcher createDispatcher() {
        return new WebRequestDispatcher(this.request, this.response, this.getTransaction());
    }
    
    protected void reset() {
        super.reset();
    }
    
    protected final void doFinish(final Throwable throwable) {
        try {
            super.doFinish(throwable);
            this.getTransaction().setThrowable(throwable, TransactionErrorPriority.TRACER);
        }
        catch (Exception e) {
            Agent.LOG.log(Level.FINE, "An error occurred calling doFinish() for dispatcher tracer with an exception", e);
        }
    }
}

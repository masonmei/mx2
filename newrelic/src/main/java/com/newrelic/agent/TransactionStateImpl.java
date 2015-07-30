// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import java.util.logging.Level;
import com.newrelic.agent.tracers.SkipTracer;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.TracerFlags;
import com.newrelic.agent.tracers.metricname.MetricNameFormats;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.TracerFactory;

public class TransactionStateImpl implements TransactionState
{
    public Tracer getTracer(final Transaction tx, final TracerFactory tracerFactory, final ClassMethodSignature sig, final Object obj, final Object... args) {
        final TransactionActivity activity = tx.getTransactionActivity();
        if (tx.isIgnore() || activity.isTracerStartLocked()) {
            return null;
        }
        final Tracer tracer = tracerFactory.getTracer(tx, sig, obj, args);
        return this.tracerStarted(tx, sig, tracer);
    }
    
    public Tracer getTracer(final Transaction tx, final String tracerFactoryName, final ClassMethodSignature sig, final Object obj, final Object... args) {
        final TracerFactory tracerFactory = ServiceFactory.getTracerService().getTracerFactory(tracerFactoryName);
        return this.getTracer(tx, tracerFactory, sig, obj, args);
    }
    
    public Tracer getTracer(final Transaction tx, final Object invocationTarget, final ClassMethodSignature sig, final String metricName, final int flags) {
        final TransactionActivity activity = tx.getTransactionActivity();
        if (tx.isIgnore() || activity.isTracerStartLocked()) {
            return null;
        }
        final MetricNameFormat mnf = MetricNameFormats.getFormatter(invocationTarget, sig, metricName, flags);
        Tracer tracer;
        if (TracerFlags.isDispatcher(flags)) {
            tracer = new OtherRootTracer(tx, sig, invocationTarget, mnf);
        }
        else {
            tracer = new DefaultTracer(tx, sig, invocationTarget, mnf, flags);
        }
        return this.tracerStarted(tx, sig, tracer);
    }
    
    private Tracer tracerStarted(final Transaction tx, final ClassMethodSignature sig, Tracer tracer) {
        if (tracer == null || tracer instanceof SkipTracer) {
            return tracer;
        }
        tracer = tx.getTransactionActivity().tracerStarted(tracer);
        if (tracer != null && Agent.LOG.isLoggable(Level.FINER)) {
            if (tracer == tx.getRootTracer()) {
                Agent.LOG.log(Level.FINER, "Transaction started {0}", new Object[] { tx });
            }
            Agent.LOG.log(Level.FINER, "Tracer ({3}) Started: {0}.{1}{2}", new Object[] { sig.getClassName(), sig.getMethodName(), sig.getMethodDesc(), tracer });
        }
        return tracer;
    }
    
    public Tracer getRootTracer() {
        return null;
    }
    
    public void resume() {
    }
    
    public void suspend() {
    }
    
    public void complete() {
    }
    
    public boolean finish(final Transaction tx, final Tracer tracer) {
        return true;
    }
    
    public void suspendRootTracer() {
    }
    
    public void asyncJobStarted(final TransactionHolder job) {
    }
    
    public void asyncJobFinished(final TransactionHolder job) {
    }
    
    public void asyncTransactionStarted(final Transaction tx, final TransactionHolder txHolder) {
    }
    
    public void asyncTransactionFinished(final TransactionActivity txa) {
    }
    
    public void mergeAsyncTracers() {
    }
    
    public void asyncJobInvalidate(final TransactionHolder job) {
    }
    
    public void setInvalidateAsyncJobs(final boolean invalidate) {
    }
}

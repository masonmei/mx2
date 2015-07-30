// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.servlet;

import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.AbstractTracerFactory;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.AbstractTracer;
import com.newrelic.agent.tracers.Tracer;
import java.util.concurrent.atomic.AtomicReference;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.TracerFactory;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.TransactionStateImpl;

public class ServletAsyncTransactionStateImpl extends TransactionStateImpl
{
    private static final ClassMethodSignature ASYNC_PROCESSING_SIG;
    private static final MetricNameFormat ASYNC_PROCESSING_FORMAT;
    private static final TracerFactory ASYNC_TRACER_FACTORY;
    private final Transaction transaction;
    private final AtomicReference<State> state;
    private volatile Tracer rootTracer;
    private volatile AbstractTracer asyncProcessingTracer;
    
    public ServletAsyncTransactionStateImpl(final Transaction tx) {
        this.state = new AtomicReference<State>(State.RUNNING);
        this.transaction = tx;
    }
    
    public Tracer getTracer(final Transaction tx, final TracerFactory tracerFactory, final ClassMethodSignature signature, final Object object, final Object... args) {
        if (this.state.compareAndSet(State.RESUMING, State.RUNNING)) {
            final Tracer tracer = this.resumeRootTracer();
            if (tracer != null) {
                return tracer;
            }
        }
        return super.getTracer(tx, tracerFactory, signature, object, args);
    }
    
    public Tracer getRootTracer() {
        if (this.state.compareAndSet(State.RESUMING, State.RUNNING)) {
            return this.resumeRootTracer();
        }
        return null;
    }
    
    public void resume() {
        if (!this.state.compareAndSet(State.SUSPENDING, State.RESUMING)) {
            return;
        }
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.finer(MessageFormat.format("Resuming transaction {0}", this.transaction));
        }
        Transaction.clearTransaction();
        Transaction.setTransaction(this.transaction);
    }
    
    public void suspendRootTracer() {
        final Transaction currentTx = Transaction.getTransaction();
        if (this.transaction != currentTx) {
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.finer(MessageFormat.format("Unable to suspend transaction {0} because it is not the current transaction {1}", this.transaction, currentTx));
            }
            return;
        }
        if (!this.state.compareAndSet(State.RUNNING, State.SUSPENDING)) {
            return;
        }
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.finer(MessageFormat.format("Transaction {0} is suspended", this.transaction));
        }
    }
    
    public void complete() {
        if (!this.state.compareAndSet(State.SUSPENDING, State.RUNNING)) {
            return;
        }
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.finer(MessageFormat.format("Completing transaction {0}", this.transaction));
        }
        final Transaction currentTx = Transaction.getTransaction();
        if (currentTx != this.transaction) {
            Transaction.clearTransaction();
            Transaction.setTransaction(this.transaction);
        }
        try {
            final Tracer tracer = this.resumeRootTracer();
            if (tracer != null) {
                tracer.finish(176, (Object)null);
            }
            if (currentTx != this.transaction) {
                Transaction.clearTransaction();
                Transaction.setTransaction(currentTx);
            }
        }
        finally {
            if (currentTx != this.transaction) {
                Transaction.clearTransaction();
                Transaction.setTransaction(currentTx);
            }
        }
    }
    
    public boolean finish(final Transaction tx, final Tracer tracer) {
        if (this.state.get() == State.SUSPENDING && tracer == tx.getRootTracer()) {
            this.suspendRootTracer(tx, tx.getRootTracer());
            return false;
        }
        return true;
    }
    
    private void suspendRootTracer(final Transaction tx, final Tracer tracer) {
        this.rootTracer = tracer;
        this.startAsyncProcessingTracer(tx);
        Transaction.clearTransaction();
    }
    
    private void startAsyncProcessingTracer(final Transaction tx) {
        if (this.asyncProcessingTracer == null) {
            this.asyncProcessingTracer = (AbstractTracer)super.getTracer(tx, ServletAsyncTransactionStateImpl.ASYNC_TRACER_FACTORY, ServletAsyncTransactionStateImpl.ASYNC_PROCESSING_SIG, null, (Object[])null);
        }
    }
    
    private Tracer resumeRootTracer() {
        this.stopAsyncProcessingTracer();
        final Tracer tracer = this.rootTracer;
        this.rootTracer = null;
        return tracer;
    }
    
    private void stopAsyncProcessingTracer() {
        if (this.asyncProcessingTracer != null) {
            this.asyncProcessingTracer.finish(176, (Object)null);
        }
        this.asyncProcessingTracer = null;
    }
    
    static {
        ASYNC_PROCESSING_SIG = new ClassMethodSignature("NR_RECORD_ASYNC_PROCESSING_CLASS", "NR_RECORD_ASYNC_PROCESSING_METHOD", "()V");
        ASYNC_PROCESSING_FORMAT = new SimpleMetricNameFormat("AsyncProcessing");
        ASYNC_TRACER_FACTORY = new AsyncTracerFactory();
    }
    
    private enum State
    {
        RESUMING, 
        RUNNING, 
        SUSPENDING;
    }
    
    private static class AsyncTracerFactory extends AbstractTracerFactory
    {
        public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
            return new DefaultTracer(tx, sig, object, ServletAsyncTransactionStateImpl.ASYNC_PROCESSING_FORMAT);
        }
    }
}

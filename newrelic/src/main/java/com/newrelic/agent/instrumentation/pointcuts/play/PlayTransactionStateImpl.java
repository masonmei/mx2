// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play;

import com.newrelic.agent.tracers.MethodExitTracerNoSkip;
import java.util.Collection;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.TracerFactory;
import com.newrelic.agent.Transaction;
import java.util.ArrayDeque;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.TransactionStateImpl;

public class PlayTransactionStateImpl extends TransactionStateImpl
{
    private static final TransactionActivity NULL_TRANSACTION_ACTIVITY;
    private static final Tracer NULL_TRACER;
    private final PlayDispatcherPointCut.PlayHttpRequest request;
    private State state;
    private final ArrayDeque<Tracer> tracers;
    private ArrayDeque<Tracer> suspendedTracers;
    
    public PlayTransactionStateImpl(final PlayDispatcherPointCut.PlayHttpRequest request) {
        this.state = State.RUNNING;
        this.tracers = new ArrayDeque<Tracer>();
        this.suspendedTracers = new ArrayDeque<Tracer>();
        this.request = request;
    }
    
    public Tracer getTracer(final Transaction tx, final TracerFactory tracerFactory, final ClassMethodSignature signature, final Object object, final Object... args) {
        if (this.state == State.RESUMING) {
            final Tracer tracer = this.removeSuspendedTracer();
            if (tracer == PlayTransactionStateImpl.NULL_TRACER) {
                return null;
            }
            if (tracer != null) {
                return tracer;
            }
            this.state = State.RUNNING;
        }
        final Tracer tracer = super.getTracer(tx, tracerFactory, signature, object, args);
        this.addTracer((tracer == null) ? PlayTransactionStateImpl.NULL_TRACER : tracer);
        return tracer;
    }
    
    public Tracer getTracer(final Transaction tx, final Object invocationTarget, final ClassMethodSignature sig, final String metricName, final int flags) {
        if (this.state == State.RESUMING) {
            final Tracer tracer = this.removeSuspendedTracer();
            if (tracer == PlayTransactionStateImpl.NULL_TRACER) {
                return null;
            }
            if (tracer != null) {
                return tracer;
            }
            this.state = State.RUNNING;
        }
        final Tracer tracer = super.getTracer(tx, invocationTarget, sig, metricName, flags);
        this.addTracer((tracer == null) ? PlayTransactionStateImpl.NULL_TRACER : tracer);
        return tracer;
    }
    
    private Tracer removeSuspendedTracer() {
        return this.suspendedTracers.pollFirst();
    }
    
    private void removeTracer(final Tracer tracer) {
        Tracer lastTracer;
        for (lastTracer = this.tracers.peekLast(); lastTracer == PlayTransactionStateImpl.NULL_TRACER; lastTracer = this.tracers.peekLast()) {
            this.tracers.pollLast();
        }
        if (lastTracer == tracer) {
            this.tracers.pollLast();
        }
    }
    
    private void addTracer(final Tracer tracer) {
        this.tracers.addLast(tracer);
    }
    
    public void resume() {
        this.state = State.RESUMING;
    }
    
    public void suspend() {
        this.state = State.SUSPENDING;
    }
    
    public void suspendRootTracer() {
        this.state = State.SUSPENDING_ROOT_TRACER;
    }
    
    public boolean finish(final Transaction tx, final Tracer tracer) {
        if (this.state == State.SUSPENDING) {
            if (tracer == tx.getRootTracer()) {
                this.saveTransaction(tx);
            }
            return false;
        }
        if (this.state == State.SUSPENDING_ROOT_TRACER && tracer == tx.getRootTracer()) {
            this.saveTransaction(tx);
            return false;
        }
        if (this.state == State.RESUMING) {
            this.suspendedTracers.clear();
            this.state = State.RUNNING;
        }
        this.removeTracer(tracer);
        return true;
    }
    
    private void saveTransaction(final Transaction tx) {
        this.suspendedTracers = new ArrayDeque<Tracer>(this.tracers);
        this.request._nr_setTransaction(tx);
        Transaction.clearTransaction();
    }
    
    static {
        NULL_TRANSACTION_ACTIVITY = null;
        NULL_TRACER = new MethodExitTracerNoSkip(null, PlayTransactionStateImpl.NULL_TRANSACTION_ACTIVITY) {
            protected void doFinish(final int opcode, final Object returnValue) {
            }
        };
    }
    
    private enum State
    {
        RESUMING, 
        RUNNING, 
        SUSPENDING, 
        SUSPENDING_ROOT_TRACER;
    }
}

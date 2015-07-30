// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.servlet.ServletAsyncTransactionStateImpl;
import java.util.logging.Level;
import com.newrelic.agent.deps.com.google.common.collect.MapMaker;
import com.newrelic.api.agent.Logger;
import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.bridge.AsyncApi;

public class AsyncApiImpl implements AsyncApi
{
    private final ConcurrentMap<Object, Transaction> asyncTransactions;
    private final Logger logger;
    
    public AsyncApiImpl(final Logger logger) {
        this.asyncTransactions = new MapMaker().weakKeys().makeMap();
        this.logger = logger;
    }
    
    public void suspendAsync(final Object asyncContext) {
        this.logger.log(Level.FINEST, "Suspend async", new Object[0]);
        if (asyncContext != null) {
            final Transaction currentTx = Transaction.getTransaction();
            final TransactionState transactionState = this.setTransactionState(currentTx);
            transactionState.suspendRootTracer();
            this.asyncTransactions.put(asyncContext, currentTx);
        }
    }
    
    private TransactionState setTransactionState(final Transaction tx) {
        TransactionState txState = tx.getTransactionState();
        if (txState instanceof ServletAsyncTransactionStateImpl) {
            return txState;
        }
        txState = new ServletAsyncTransactionStateImpl(tx);
        tx.setTransactionState(txState);
        return txState;
    }
    
    public com.newrelic.agent.bridge.Transaction resumeAsync(final Object asyncContext) {
        this.logger.log(Level.FINEST, "Resume async", new Object[0]);
        if (asyncContext != null) {
            final Transaction suspendedTx = this.asyncTransactions.get(asyncContext);
            if (suspendedTx != null) {
                suspendedTx.getTransactionState().resume();
                if (suspendedTx.isStarted()) {
                    suspendedTx.getTransactionState().getRootTracer();
                    return (com.newrelic.agent.bridge.Transaction)new BoundTransactionApiImpl(suspendedTx);
                }
            }
        }
        return (com.newrelic.agent.bridge.Transaction)new TransactionApiImpl();
    }
    
    public void completeAsync(final Object asyncContext) {
        this.logger.log(Level.FINEST, "Complete async", new Object[0]);
        if (asyncContext == null) {
            return;
        }
        final Transaction transaction = this.asyncTransactions.remove(asyncContext);
        if (transaction != null) {
            transaction.getTransactionState().complete();
        }
    }
    
    public void errorAsync(final Object asyncContext, final Throwable t) {
        this.logger.log(Level.FINEST, "Error async", new Object[0]);
        if (asyncContext == null || t == null) {
            return;
        }
        final Transaction transaction = this.asyncTransactions.get(asyncContext);
        if (transaction != null) {
            transaction.setThrowable(t, TransactionErrorPriority.API);
        }
    }
    
    public void finishRootTracer() {
        final Transaction currentTx = Transaction.getTransaction();
        final Tracer rootTracer = currentTx.getRootTracer();
        if (rootTracer != null) {
            rootTracer.finish(177, (Object)null);
        }
    }
}

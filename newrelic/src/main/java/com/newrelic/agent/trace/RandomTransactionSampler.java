// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.service.ServiceFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.atomic.AtomicReference;
import com.newrelic.agent.TransactionData;

public class RandomTransactionSampler implements ITransactionSampler
{
    private static final TransactionData FINISHED;
    private final int maxTraces;
    private final AtomicReference<TransactionData> expensiveTransaction;
    private int tracesSent;
    
    protected RandomTransactionSampler(final int maxTraces) {
        this.expensiveTransaction = new AtomicReference<TransactionData>();
        this.maxTraces = maxTraces;
    }
    
    public boolean noticeTransaction(final TransactionData td) {
        if (this.expensiveTransaction.compareAndSet(null, td)) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Captured random transaction trace for {0} {1}", td.getApplicationName(), td);
                Agent.LOG.finer(msg);
            }
            return true;
        }
        return false;
    }
    
    public List<TransactionTrace> harvest(final String appName) {
        TransactionData td = this.expensiveTransaction.get();
        if (td == RandomTransactionSampler.FINISHED) {
            return Collections.emptyList();
        }
        if (td == null) {
            return Collections.emptyList();
        }
        if (td.getApplicationName() != appName) {
            return Collections.emptyList();
        }
        if (this.shouldFinish()) {
            td = this.expensiveTransaction.getAndSet(RandomTransactionSampler.FINISHED);
            this.stop();
        }
        else {
            td = this.expensiveTransaction.getAndSet(null);
        }
        ++this.tracesSent;
        return this.getTransactionTrace(td);
    }
    
    private List<TransactionTrace> getTransactionTrace(final TransactionData td) {
        final TransactionTrace trace = TransactionTrace.getTransactionTrace(td);
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Sending random transaction trace for {0}: {1}", td.getApplicationName(), td);
            Agent.LOG.finer(msg);
        }
        final List<TransactionTrace> traces = new ArrayList<TransactionTrace>(1);
        traces.add(trace);
        return traces;
    }
    
    private boolean shouldFinish() {
        return this.tracesSent >= this.maxTraces;
    }
    
    public void stop() {
        ServiceFactory.getTransactionTraceService().removeTransactionTraceSampler(this);
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Stopped random transaction tracing: max traces={1}", this.maxTraces);
            Agent.LOG.finer(msg);
        }
    }
    
    private void start() {
        ServiceFactory.getTransactionTraceService().addTransactionTraceSampler(this);
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Started random transaction tracing: max traces={1}", this.maxTraces);
            Agent.LOG.finer(msg);
        }
    }
    
    public long getMaxDurationInNanos() {
        return Long.MAX_VALUE;
    }
    
    public static RandomTransactionSampler startSampler(final int maxTraces) {
        final RandomTransactionSampler transactionSampler = new RandomTransactionSampler(maxTraces);
        transactionSampler.start();
        return transactionSampler;
    }
    
    static {
        FINISHED = new TransactionData(null, 0);
    }
}

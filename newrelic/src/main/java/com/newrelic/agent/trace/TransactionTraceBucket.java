// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import java.util.ArrayList;
import java.util.List;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Collections;
import java.util.HashMap;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.locks.Lock;
import com.newrelic.agent.TransactionData;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Map;

public class TransactionTraceBucket implements ITransactionSampler
{
    private static final int NO_TRACE_LIMIT = 5;
    private volatile Map<String, Long> tracedTransactions;
    private final AtomicReference<TransactionData> expensiveTransaction;
    private volatile long maxDurationInNanos;
    private final int topN;
    private int noTraceCount;
    private final Lock readLock;
    private final Lock writeLock;
    
    public TransactionTraceBucket() {
        this.expensiveTransaction = new AtomicReference<TransactionData>();
        this.topN = ServiceFactory.getConfigService().getDefaultAgentConfig().getTransactionTracerConfig().getTopN();
        this.tracedTransactions = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)new HashMap<String, Long>(this.topN));
        final ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    public boolean noticeTransaction(final TransactionData td) {
        if (td.getDuration() <= td.getTransactionTracerConfig().getTransactionThresholdInNanos()) {
            if (Agent.LOG.isLoggable(Level.FINER)) {
                final String msg = MessageFormat.format("Transaction trace threshold not exceeded {0}", td);
                Agent.LOG.finer(msg);
            }
            return false;
        }
        if (td.getDuration() <= this.maxDurationInNanos) {
            return false;
        }
        this.readLock.lock();
        try {
            final boolean noticeTransactionUnderLock = this.noticeTransactionUnderLock(td);
            this.readLock.unlock();
            return noticeTransactionUnderLock;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private boolean noticeTransactionUnderLock(final TransactionData td) {
        final Long lastDuration = this.tracedTransactions.get(td.getBlameMetricName());
        if (lastDuration != null && td.getDuration() <= lastDuration) {
            return false;
        }
        while (true) {
            final TransactionData current = this.expensiveTransaction.get();
            if (current != null && current.getDuration() >= td.getDuration()) {
                return false;
            }
            if (this.expensiveTransaction.compareAndSet(current, td)) {
                this.maxDurationInNanos = td.getDuration();
                if (Agent.LOG.isLoggable(Level.FINER)) {
                    final String msg = MessageFormat.format("Captured expensive transaction trace for {0} {1}", td.getApplicationName(), td);
                    Agent.LOG.finer(msg);
                }
                return true;
            }
        }
    }
    
    public List<TransactionTrace> harvest(final String appName) {
        TransactionData td = null;
        this.writeLock.lock();
        try {
            td = this.harvestUnderLock(appName);
            this.writeLock.unlock();
        }
        finally {
            this.writeLock.unlock();
        }
        if (td == null) {
            return Collections.emptyList();
        }
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("Sending transaction trace for {0} {1}", td.getApplicationName(), td);
            Agent.LOG.finer(msg);
        }
        final TransactionTrace trace = TransactionTrace.getTransactionTrace(td);
        final List<TransactionTrace> traces = new ArrayList<TransactionTrace>(1);
        traces.add(trace);
        return traces;
    }
    
    private TransactionData harvestUnderLock(final String appName) {
        this.maxDurationInNanos = 0L;
        final TransactionData td = this.expensiveTransaction.getAndSet(null);
        this.noticeTracedTransaction(td);
        return td;
    }
    
    private void noticeTracedTransaction(final TransactionData td) {
        if (this.topN == 0) {
            return;
        }
        final int size = this.tracedTransactions.size();
        if (td == null) {
            ++this.noTraceCount;
            if (this.noTraceCount >= 5 && size > 0) {
                this.noTraceCount = 0;
                this.tracedTransactions = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)new HashMap<String, Long>(this.topN));
            }
            return;
        }
        this.noTraceCount = 0;
        final Map<String, Long> ttMap = new HashMap<String, Long>(this.topN);
        if (size < this.topN) {
            ttMap.putAll(this.tracedTransactions);
        }
        ttMap.put(td.getBlameMetricName(), td.getDuration());
        this.tracedTransactions = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)ttMap);
    }
    
    public void stop() {
        this.expensiveTransaction.set(null);
        this.tracedTransactions.clear();
    }
    
    public long getMaxDurationInNanos() {
        return this.maxDurationInNanos;
    }
}

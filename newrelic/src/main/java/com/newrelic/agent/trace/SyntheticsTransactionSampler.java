// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.trace;

import com.newrelic.agent.Transaction;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;
import com.newrelic.agent.TransactionData;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SyntheticsTransactionSampler implements ITransactionSampler
{
    private final ConcurrentLinkedQueue<TransactionData> pending;
    private final AtomicInteger pendingCount;
    static final int MAX_SYNTHETIC_TRANSACTION_PER_HARVEST = 20;
    private static final TransactionData queueMarker;
    
    public SyntheticsTransactionSampler() {
        this.pending = new ConcurrentLinkedQueue<TransactionData>();
        this.pendingCount = new AtomicInteger(0);
    }
    
    public boolean noticeTransaction(final TransactionData td) {
        if (td.isSyntheticTransaction()) {
            if (this.pendingCount.get() < 20) {
                this.pendingCount.incrementAndGet();
                this.pending.add(td);
                final String msg = MessageFormat.format("Sampled Synthetics Transaction: {0}", td);
                Agent.LOG.finest(msg);
                return true;
            }
            Agent.LOG.log(Level.FINER, "Dropped Synthetic TT for app {0}", new Object[] { td.getApplicationName() });
        }
        return false;
    }
    
    public List<TransactionTrace> harvest(final String appName) {
        final List<TransactionTrace> result = new LinkedList<TransactionTrace>();
        if (appName == null) {
            return result;
        }
        this.pending.add(SyntheticsTransactionSampler.queueMarker);
        int removedCount = 0;
        TransactionData queued;
        while ((queued = this.pending.poll()) != SyntheticsTransactionSampler.queueMarker) {
            if (appName.equals(queued.getApplicationName())) {
                final TransactionTrace tt = TransactionTrace.getTransactionTrace(queued);
                tt.setSyntheticsResourceId(queued.getSyntheticsResourceId());
                ++removedCount;
                result.add(tt);
            }
            else {
                this.pending.add(queued);
            }
        }
        this.pendingCount.addAndGet(-removedCount);
        return result;
    }
    
    public void stop() {
    }
    
    public long getMaxDurationInNanos() {
        return 1L;
    }
    
    int getPendingCount() {
        return this.pendingCount.get();
    }
    
    static {
        queueMarker = new TransactionData(null, 0);
    }
}

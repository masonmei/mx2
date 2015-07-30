// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import com.newrelic.agent.stats.StatsEngine;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.newrelic.agent.stats.StatsWork;
import com.newrelic.agent.stats.StatsService;
import java.util.Iterator;
import com.newrelic.agent.transaction.MergeStatsEngineResolvingScope;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.List;
import com.newrelic.agent.service.AbstractService;

public class TransactionService extends AbstractService implements HarvestListener
{
    private static final ThreadLocal<Boolean> NOTICE_REQUEST_THREAD;
    private static final ThreadLocal<Boolean> NOTICE_BACKGROUND_THREAD;
    private final List<TransactionListener> transactionListeners;
    private final Map<Long, Transaction> transactionThreadMap;
    
    public TransactionService() {
        super(TransactionService.class.getSimpleName());
        this.transactionListeners = new CopyOnWriteArrayList<TransactionListener>();
        this.transactionThreadMap = new ConcurrentHashMap<Long, Transaction>();
    }
    
    public static void noticeRequestThread(final long threadId) {
        if (TransactionService.NOTICE_REQUEST_THREAD.get()) {
            return;
        }
        ServiceFactory.getThreadService().noticeRequestThread(threadId);
        TransactionService.NOTICE_REQUEST_THREAD.set(Boolean.TRUE);
    }
    
    public static void noticeBackgroundThread(final long threadId) {
        if (TransactionService.NOTICE_BACKGROUND_THREAD.get()) {
            return;
        }
        ServiceFactory.getThreadService().noticeBackgroundThread(threadId);
        TransactionService.NOTICE_BACKGROUND_THREAD.set(Boolean.TRUE);
    }
    
    public void processTransaction(final TransactionData transactionData, final TransactionStats transactionStats) {
        try {
            this.doProcessTransaction(transactionData, transactionStats);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Error recording transaction \"{0}\": {1}", transactionData.getBlameMetricName(), e);
            if (this.getLogger().isLoggable(Level.FINER)) {
                this.getLogger().log(Level.FINER, msg, e);
            }
            else {
                this.getLogger().warning(msg);
            }
        }
    }
    
    private void doProcessTransaction(final TransactionData transactionData, final TransactionStats transactionStats) {
        if (!ServiceFactory.getServiceManager().isStarted() || !ServiceFactory.getAgent().isEnabled()) {
            return;
        }
        if (Agent.isDebugEnabled()) {
            this.getLogger().finer("Recording metrics for " + transactionData);
        }
        final String transactionSizeMetric = "Supportability/TransactionSize";
        final boolean sizeLimitExceeded = transactionData.getAgentAttributes().get("size_limit") != null;
        transactionStats.getUnscopedStats().getStats(transactionSizeMetric).recordDataPoint(transactionData.getTransactionSize());
        if (sizeLimitExceeded) {
            transactionStats.getUnscopedStats().getStats("Supportability/TransactionSizeClamp").incrementCallCount();
        }
        if (transactionData.isWebTransaction()) {
            noticeRequestThread(transactionData.getThreadId());
        }
        else {
            noticeBackgroundThread(transactionData.getThreadId());
        }
        if (transactionData.getDispatcher() != null) {
            for (final TransactionListener listener : this.transactionListeners) {
                listener.dispatcherTransactionFinished(transactionData, transactionStats);
            }
        }
        else if (Agent.isDebugEnabled()) {
            this.getLogger().finer("Skipping transaction trace for " + transactionData);
        }
        final StatsService statsService = ServiceFactory.getStatsService();
        final StatsWork statsWork = new MergeStatsEngineResolvingScope(transactionData.getBlameMetricName(), transactionData.getApplicationName(), transactionStats);
        statsService.doStatsWork(statsWork);
    }
    
    protected void doStart() {
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() {
        this.transactionListeners.clear();
        this.transactionThreadMap.clear();
    }
    
    public void addTransaction(final Transaction tx) {
        final long id = Thread.currentThread().getId();
        this.transactionThreadMap.put(id, tx);
    }
    
    public void removeTransaction() {
        this.transactionThreadMap.remove(Thread.currentThread().getId());
    }
    
    public Set<Long> getRunningThreadIds() {
        final Set<Long> runningThreadIds = new HashSet<Long>();
        for (final Map.Entry<Long, Transaction> entry : this.transactionThreadMap.entrySet()) {
            final Transaction tx = entry.getValue();
            if (tx.isStarted()) {
                runningThreadIds.add(entry.getKey());
            }
        }
        return runningThreadIds;
    }
    
    public Set<Long> getThreadIds() {
        return new HashSet<Long>(this.transactionThreadMap.keySet());
    }
    
    public void addTransactionListener(final TransactionListener listener) {
        this.transactionListeners.add(listener);
    }
    
    public void removeTransactionListener(final TransactionListener listener) {
        this.transactionListeners.remove(listener);
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        final Set<Long> threadIds = this.transactionThreadMap.keySet();
        final Iterator<Long> it = threadIds.iterator();
        while (it.hasNext()) {
            final long threadId = it.next();
            if (this.hasThreadTerminated(threadId)) {
                it.remove();
            }
        }
    }
    
    private boolean hasThreadTerminated(final long threadId) {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, 0);
        return threadInfo == null || threadInfo.getThreadState() == Thread.State.TERMINATED;
    }
    
    public void afterHarvest(final String appName) {
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public Transaction getTransaction(final boolean createIfNotExists) {
        return Transaction.getTransaction(createIfNotExists);
    }
    
    static {
        NOTICE_REQUEST_THREAD = new ThreadLocal<Boolean>() {
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };
        NOTICE_BACKGROUND_THREAD = new ThreadLocal<Boolean>() {
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };
    }
}

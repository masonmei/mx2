// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.TransactionData;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.agent.service.ServiceFactory;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.Queue;
import java.util.Map;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;
import com.newrelic.agent.TransactionListener;

public class KeyTransactionProfile implements IProfile, TransactionListener, JSONStreamAware
{
    private static final int CAPACITY = 100;
    private static final long THREAD_CHECK_INTERVAL_IN_NANOS;
    private final IProfile delegate;
    private final String keyTransaction;
    private final Map<Long, Queue<StackTraceHolder>> pendingStackTraces;
    private final Queue<StackTraceHolder> releasedStackTraces;
    private long lastThreadCheck;
    private final Set<Long> requestThreads;
    
    public KeyTransactionProfile(final ProfilerParameters parameters) {
        this.pendingStackTraces = new ConcurrentHashMap<Long, Queue<StackTraceHolder>>();
        this.releasedStackTraces = new ConcurrentLinkedQueue<StackTraceHolder>();
        this.lastThreadCheck = System.nanoTime();
        this.requestThreads = new HashSet<Long>();
        this.keyTransaction = parameters.getKeyTransaction();
        this.delegate = new Profile(parameters);
    }
    
    IProfile getDelegate() {
        return this.delegate;
    }
    
    public void start() {
        ServiceFactory.getTransactionService().addTransactionListener(this);
        this.delegate.start();
    }
    
    public void end() {
        ServiceFactory.getTransactionService().removeTransactionListener(this);
        this.pendingStackTraces.clear();
        this.releaseStackTraces();
        this.delegate.end();
    }
    
    public ProfilerParameters getProfilerParameters() {
        return this.delegate.getProfilerParameters();
    }
    
    public int getSampleCount() {
        return this.delegate.getSampleCount();
    }
    
    public Long getProfileId() {
        return this.delegate.getProfileId();
    }
    
    public ProfileTree getProfileTree(final ThreadType threadType) {
        return this.delegate.getProfileTree(threadType);
    }
    
    public void writeJSONString(final Writer out) throws IOException {
        this.delegate.writeJSONString(out);
    }
    
    public int trimBy(final int count) {
        return this.delegate.trimBy(count);
    }
    
    public long getStartTimeMillis() {
        return this.delegate.getStartTimeMillis();
    }
    
    public long getEndTimeMillis() {
        return this.delegate.getEndTimeMillis();
    }
    
    private void releaseStackTraces() {
        while (true) {
            final StackTraceHolder holder = this.releasedStackTraces.poll();
            if (holder == null) {
                break;
            }
            this.delegate.addStackTrace(holder.getThreadId(), holder.isRunnable(), holder.getType(), holder.getStackTrace());
        }
    }
    
    public void dispatcherTransactionFinished(final TransactionData td, final TransactionStats stats) {
        try {
            this.doDispatcherTransactionFinished(td, stats);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Error releasing stack traces for \"{0}\": {1}", td.getBlameMetricName(), e);
            if (Agent.LOG.isLoggable(Level.FINEST)) {
                Agent.LOG.log(Level.FINEST, msg, e);
            }
            else {
                Agent.LOG.finer(msg);
            }
        }
    }
    
    private void doDispatcherTransactionFinished(final TransactionData td, final TransactionStats stats) {
        final Queue<StackTraceHolder> holderQueue = this.getHolderQueue(td.getThreadId());
        if (holderQueue == null) {
            return;
        }
        final boolean isKeyTransaction = this.isKeyTransaction(td);
        while (true) {
            final StackTraceHolder holder = holderQueue.poll();
            if (holder == null) {
                break;
            }
            if (td.getStartTimeInNanos() > holder.getStackTraceTime()) {
                continue;
            }
            if (td.getEndTimeInNanos() < holder.getStackTraceTime()) {
                break;
            }
            if (!isKeyTransaction) {
                continue;
            }
            this.releasedStackTraces.add(holder);
        }
    }
    
    private boolean isKeyTransaction(final TransactionData td) {
        return this.keyTransaction.equals(td.getBlameMetricName());
    }
    
    public void beforeSampling() {
        this.checkDeadThreads();
        this.releaseStackTraces();
        this.delegate.beforeSampling();
    }
    
    private void checkDeadThreads() {
        final long currentTime = System.nanoTime();
        if (currentTime - this.lastThreadCheck > KeyTransactionProfile.THREAD_CHECK_INTERVAL_IN_NANOS) {
            this.lastThreadCheck = currentTime;
            final Set<Long> liveRequestThreads = ServiceFactory.getThreadService().getRequestThreadIds();
            final List<Long> deadRequestThreads = new ArrayList<Long>(this.requestThreads);
            deadRequestThreads.removeAll(liveRequestThreads);
            for (final Long threadId : deadRequestThreads) {
                this.removeHolderQueue(threadId);
            }
        }
    }
    
    public void addStackTrace(final long threadId, final boolean runnable, final ThreadType type, final StackTraceElement... stackTrace) {
        if (type != ThreadType.BasicThreadType.REQUEST && type != ThreadType.BasicThreadType.BACKGROUND) {
            return;
        }
        final StackTraceHolder holder = new StackTraceHolder(threadId, runnable, type, stackTrace);
        final Queue<StackTraceHolder> holderQueue = this.getOrCreateHolderQueue(threadId);
        holderQueue.offer(holder);
    }
    
    private Queue<StackTraceHolder> getHolderQueue(final long threadId) {
        return this.pendingStackTraces.get(threadId);
    }
    
    private Queue<StackTraceHolder> getOrCreateHolderQueue(final long threadId) {
        Queue<StackTraceHolder> holderQueue = this.pendingStackTraces.get(threadId);
        if (holderQueue == null) {
            holderQueue = new LinkedBlockingQueue<StackTraceHolder>(100);
            this.pendingStackTraces.put(threadId, holderQueue);
            this.requestThreads.add(threadId);
        }
        return holderQueue;
    }
    
    private void removeHolderQueue(final long threadId) {
        this.pendingStackTraces.remove(threadId);
        this.requestThreads.remove(threadId);
    }
    
    public void markInstrumentedMethods() {
    }
    
    static {
        THREAD_CHECK_INTERVAL_IN_NANOS = TimeUnit.NANOSECONDS.convert(300L, TimeUnit.SECONDS);
    }
    
    private static class StackTraceHolder
    {
        private final long threadId;
        private final boolean runnable;
        private final ThreadType type;
        private final long stackTraceTime;
        private final StackTraceElement[] stackTrace;
        
        private StackTraceHolder(final long threadId, final boolean runnable, final ThreadType type, final StackTraceElement... stackTrace) {
            this.threadId = threadId;
            this.runnable = runnable;
            this.type = type;
            this.stackTrace = stackTrace;
            this.stackTraceTime = System.nanoTime();
        }
        
        public long getThreadId() {
            return this.threadId;
        }
        
        public boolean isRunnable() {
            return this.runnable;
        }
        
        public ThreadType getType() {
            return this.type;
        }
        
        public StackTraceElement[] getStackTrace() {
            return this.stackTrace;
        }
        
        public long getStackTraceTime() {
            return this.stackTraceTime;
        }
    }
}

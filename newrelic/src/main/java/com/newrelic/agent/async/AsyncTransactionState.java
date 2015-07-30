// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.async;

import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.deps.com.google.common.base.Strings;
import com.newrelic.agent.instrumentation.pointcuts.scala.TransactionHolderDispatcherPointCut;
import java.util.Arrays;
import com.newrelic.agent.TransactionState;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Map;
import com.newrelic.agent.stats.TransactionStats;
import java.util.Iterator;
import com.newrelic.agent.tracers.AbstractTracer;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.Tracer;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReference;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.TransactionActivity;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.TransactionStateImpl;

public class AsyncTransactionState extends TransactionStateImpl
{
    private static final String ASYNC_WAIT = "Async Wait";
    private static final ClassMethodSignature ASYNC_WAIT_SIG;
    private static final MetricNameFormat ASYNC_WAIT_FORMAT;
    private static final Object[] ASYNC_TRACER_ARGS;
    private static final int MAX_DEPTH = 150;
    private final AtomicBoolean isSuspended;
    private final AtomicBoolean isComplete;
    private final Collection<TransactionActivity> asyncTransactionActivitiesComplete;
    private final Collection<Transaction> asyncTransactions;
    private final Collection<TransactionHolder> asyncJobs;
    private final AtomicReference<TransactionActivity> transactionActivityRef;
    private final AtomicReference<TransactionActivity> parentTransactionActivityRef;
    private volatile long asyncStartTimeInNanos;
    private volatile long asyncFinishTimeInNanos;
    private final AtomicBoolean invalidateAsyncJobs;
    private final Deque<TransactionActivity> mergingActivities;
    private final Deque<AsyncTracer> mergingActivityAsyncTracer;
    
    public AsyncTransactionState(final TransactionActivity txa) {
        this(txa, null);
    }
    
    public AsyncTransactionState(final TransactionActivity txa, final TransactionActivity parentTransactionActivity) {
        this.isSuspended = new AtomicBoolean(false);
        this.isComplete = new AtomicBoolean(false);
        this.asyncTransactionActivitiesComplete = new ConcurrentLinkedQueue<TransactionActivity>();
        this.asyncTransactions = new ConcurrentLinkedQueue<Transaction>();
        this.asyncJobs = new ConcurrentLinkedQueue<TransactionHolder>();
        this.transactionActivityRef = new AtomicReference<TransactionActivity>();
        this.parentTransactionActivityRef = new AtomicReference<TransactionActivity>();
        this.asyncStartTimeInNanos = -1L;
        this.asyncFinishTimeInNanos = -1L;
        this.invalidateAsyncJobs = new AtomicBoolean();
        this.mergingActivities = new LinkedList<TransactionActivity>();
        this.mergingActivityAsyncTracer = new LinkedList<AsyncTracer>();
        this.transactionActivityRef.set(txa);
        this.parentTransactionActivityRef.set(parentTransactionActivity);
    }
    
    public boolean finish(final Transaction tx, final Tracer tracer) {
        if (tracer == tx.getRootTracer() && this.isAsync() && !this.isComplete()) {
            this.asyncStartTimeInNanos = System.nanoTime();
            tx.getTransactionActivity().recordCpu();
            Transaction.clearTransaction();
            this.isSuspended.set(true);
            if (Agent.LOG.isFinestEnabled()) {
                Agent.LOG.finest(MessageFormat.format("Suspended transaction {0}", this.transactionActivityRef.get()));
            }
            return false;
        }
        return true;
    }
    
    boolean isAsync() {
        return !this.asyncJobs.isEmpty() || !this.asyncTransactions.isEmpty();
    }
    
    private void tryComplete(final boolean finishRootTracer) {
        if (this.isAsyncComplete() && this.isComplete.compareAndSet(false, true)) {
            try {
                this.doComplete(finishRootTracer);
            }
            catch (Exception ex) {
                final String msg = MessageFormat.format("Failed to complete transaction {0}: {1}", this.transactionActivityRef.get(), ex);
                if (Agent.LOG.isFinestEnabled()) {
                    Agent.LOG.log(Level.FINEST, msg, ex);
                }
                else {
                    Agent.LOG.finer(msg);
                }
            }
        }
        if (this.parentTransactionActivityRef.get() == null && Agent.LOG.isFinerEnabled()) {
            this.printIncompleteTransactionGraph();
        }
    }
    
    public boolean isComplete() {
        return this.isComplete.get();
    }
    
    private boolean isAsyncComplete() {
        return this.asyncJobs.isEmpty() && this.asyncTransactions.isEmpty() && this.isSuspended.get();
    }
    
    private void doComplete(final boolean finishRootTracer) {
        Transaction currentTx = Transaction.getTransaction();
        if (currentTx.isStarted()) {
            if (currentTx == this.transactionActivityRef.get().getTransaction()) {
                currentTx = null;
            }
            else {
                Transaction.clearTransaction();
                Transaction.setTransaction(this.transactionActivityRef.get().getTransaction());
            }
        }
        this.asyncFinishTimeInNanos = System.nanoTime();
        this.completeTransaction(finishRootTracer);
        if (Agent.LOG.isFinestEnabled()) {
            Agent.LOG.finest(MessageFormat.format("Completed transaction {0}", this.transactionActivityRef.get()));
        }
        if (currentTx != null) {
            Transaction.clearTransaction();
            Transaction.setTransaction(currentTx);
        }
    }
    
    private void completeTransaction(final boolean finishRootTracer) {
        this.mergeAsyncTransactionData();
        if (finishRootTracer) {
            this.finishTracer((AbstractTracer)this.transactionActivityRef.get().getRootTracer());
        }
        final TransactionActivity parentTxActivity = this.parentTransactionActivityRef.get();
        if (parentTxActivity != null) {
            parentTxActivity.getTransaction().getTransactionState().asyncTransactionFinished(this.transactionActivityRef.get());
        }
    }
    
    private void mergeAsyncTransactionData() {
        for (final TransactionActivity txa : this.asyncTransactionActivitiesComplete) {
            this.mergeAsyncTransactionData(txa);
        }
    }
    
    private void mergeAsyncTransactionData(final TransactionActivity childActivity) {
        if (childActivity == this.transactionActivityRef.get()) {
            Agent.LOG.fine("Cannot merge transaction into itself: " + childActivity);
            return;
        }
        this.mergeStats(childActivity);
        this.mergeParameters(childActivity.getTransaction());
    }
    
    private void mergeStats(final TransactionActivity childActivity) {
        final TransactionActivity txa = this.transactionActivityRef.get();
        final TransactionStats stats = txa.getTransactionStats();
        stats.getScopedStats().mergeStats(childActivity.getTransactionStats().getScopedStats());
        childActivity.getTransactionStats().getUnscopedStats().getStatsMap().remove("GC/cumulative");
        stats.getUnscopedStats().mergeStats(childActivity.getTransactionStats().getUnscopedStats());
    }
    
    private void mergeParameters(final Transaction tx) {
        final Transaction transaction = this.transactionActivityRef.get().getTransaction();
        final Long cpuTime = tx.getIntrinsicAttributes().get("cpu_time");
        if (cpuTime != null) {
            transaction.addTotalCpuTimeForLegacy(cpuTime);
        }
        if (transaction.getUserAttributes().size() + tx.getUserAttributes().size() <= transaction.getAgentConfig().getMaxUserParameters()) {
            transaction.getUserAttributes().putAll(tx.getUserAttributes());
        }
    }
    
    public void mergeAsyncTracers() {
        if (this.asyncTransactionActivitiesComplete.isEmpty()) {
            return;
        }
        if (!this.asyncTransactionActivitiesComplete.isEmpty()) {
            final AsyncTracer asyncWaitTracer = new AsyncTracer(this.transactionActivityRef.get(), this.transactionActivityRef.get(), AsyncTransactionState.ASYNC_WAIT_SIG, AsyncTransactionState.ASYNC_WAIT_FORMAT, this.asyncStartTimeInNanos, this.asyncFinishTimeInNanos);
            asyncWaitTracer.setAttribute("nr_async_wait", true);
            this.transactionActivityRef.get().tracerStarted(asyncWaitTracer);
            this.mergingActivityAsyncTracer.push(asyncWaitTracer);
        }
        for (final TransactionActivity childActivity : this.asyncTransactionActivitiesComplete) {
            this.mergingActivities.push(childActivity);
        }
        while (!this.mergingActivities.isEmpty()) {
            final TransactionActivity txa = this.mergingActivities.pop();
            AsyncTransactionState asyncState = null;
            if (txa.getTransaction().getTransactionState() instanceof AsyncTransactionState) {
                asyncState = (AsyncTransactionState)txa.getTransaction().getTransactionState();
            }
            if (asyncState != null) {
                while (this.mergingActivityAsyncTracer.peek().getTracerParentActivty() != asyncState.parentTransactionActivityRef.get()) {
                    this.finishTracer(this.mergingActivityAsyncTracer.pop());
                }
            }
            this.mergeActivityTracers(this.mergingActivityAsyncTracer.peek(), txa);
            if (asyncState != null) {
                if (!asyncState.asyncTransactionActivitiesComplete.isEmpty()) {
                    final AsyncTracer asyncWaitTracer2 = new AsyncTracer(this.transactionActivityRef.get(), txa, AsyncTransactionState.ASYNC_WAIT_SIG, AsyncTransactionState.ASYNC_WAIT_FORMAT, asyncState.asyncStartTimeInNanos, asyncState.asyncFinishTimeInNanos);
                    asyncWaitTracer2.setAttribute("nr_async_wait", true);
                    this.transactionActivityRef.get().tracerStarted(asyncWaitTracer2);
                    this.mergingActivityAsyncTracer.push(asyncWaitTracer2);
                }
                for (final TransactionActivity childActivity2 : asyncState.asyncTransactionActivitiesComplete) {
                    this.mergingActivities.push(childActivity2);
                }
                asyncState.asyncTransactionActivitiesComplete.clear();
            }
        }
        while (!this.mergingActivityAsyncTracer.isEmpty()) {
            this.finishTracer(this.mergingActivityAsyncTracer.pop());
        }
    }
    
    private void finishTracer(final AbstractTracer tracer) {
        if (tracer != null) {
            tracer.invoke("s", null, AsyncTransactionState.ASYNC_TRACER_ARGS);
        }
    }
    
    private void mergeActivityTracers(final AsyncTracer asyncWaitTracer, final TransactionActivity childActivity) {
        if (childActivity == this.transactionActivityRef.get()) {
            Agent.LOG.fine("Cannot merge transaction into itself: " + childActivity);
            return;
        }
        this.mergeTracers(asyncWaitTracer, childActivity);
    }
    
    private void mergeTracers(final AsyncTracer asyncWaitTracer, final TransactionActivity childActivity) {
        final TransactionActivity txa = this.transactionActivityRef.get();
        final Tracer rootTracer = childActivity.getRootTracer();
        rootTracer.setParentTracer(asyncWaitTracer);
        txa.addTracer(rootTracer);
        final List<Tracer> tracers = this.getInterestingTracers(childActivity);
        for (final Tracer tracer : tracers) {
            txa.addTracer(tracer);
        }
        asyncWaitTracer.childTracerFinished(rootTracer);
    }
    
    private List<Tracer> getInterestingTracers(final TransactionActivity txa) {
        final Tracer rootTracer = txa.getRootTracer();
        final List<Tracer> tracers = txa.getTracers();
        final Set<Tracer> interestingTracers = new HashSet<Tracer>();
        for (final Tracer tracer : tracers) {
            if (this.isInteresting(tracer.getMetricName())) {
                interestingTracers.add(tracer);
                for (Tracer parentTracer = tracer.getParentTracer(); parentTracer != null && parentTracer != rootTracer; parentTracer = parentTracer.getParentTracer()) {
                    interestingTracers.add(parentTracer);
                }
            }
        }
        return this.getInterestingTracersInStartOrder(tracers, interestingTracers);
    }
    
    private boolean isInteresting(final String metricName) {
        if (metricName != null) {
            if (metricName.startsWith("Async Wait")) {
                return false;
            }
            if (metricName.startsWith("Java/scala.concurrent")) {
                return false;
            }
            if (metricName.startsWith("Java/scala.collection")) {
                return false;
            }
            if (metricName.startsWith("Java/play.api.libs.concurrent")) {
                return false;
            }
            if (metricName.startsWith("Java/play.api.libs.iteratee")) {
                return false;
            }
            if (metricName.startsWith("Java/play.core.server.netty.PlayDefaultUpstreamHandler")) {
                return false;
            }
            if (metricName.startsWith("Java/play.libs.F")) {
                return false;
            }
            if (metricName.startsWith("Java/akka.pattern.PromiseActorRef")) {
                return false;
            }
        }
        return true;
    }
    
    private List<Tracer> getInterestingTracersInStartOrder(final List<Tracer> tracers, final Set<Tracer> interestingTracers) {
        final List<Tracer> result = new ArrayList<Tracer>(interestingTracers.size());
        for (final Tracer tracer : tracers) {
            if (interestingTracers.contains(tracer)) {
                result.add(tracer);
            }
        }
        return result;
    }
    
    public void asyncTransactionStarted(final Transaction tx, final TransactionHolder txHolder) {
        if (this.isComplete()) {
            return;
        }
        if (this.transactionActivityRef.get().getTransaction() == tx) {
            Agent.LOG.fine("Cannot start async transaction of itself: " + tx);
            return;
        }
        final boolean added = this.addAsyncTransaction(tx);
        if (added && Agent.LOG.isFinestEnabled()) {
            final String msg = MessageFormat.format("Async transaction started for {0} by {1}: {2}", this.transactionActivityRef.get(), txHolder, tx);
            Agent.LOG.finest(msg);
        }
    }
    
    private boolean addAsyncTransaction(final Transaction tx) {
        return !this.asyncTransactions.contains(tx) && this.asyncTransactions.add(tx);
    }
    
    public void asyncTransactionFinished(final TransactionActivity txa) {
        if (this.isComplete()) {
            return;
        }
        if (this.transactionActivityRef.get() == txa) {
            Agent.LOG.fine("Cannot finish async transaction of itself: " + txa);
            return;
        }
        final boolean rootIgnored = !txa.getRootTracer().isTransactionSegment();
        final boolean noTracers = txa.getTracers().isEmpty();
        final TransactionState transactionState = txa.getTransaction().getTransactionState();
        final boolean noChildren = !(transactionState instanceof AsyncTransactionState) || ((AsyncTransactionState)transactionState).asyncTransactionActivitiesComplete.isEmpty();
        if (rootIgnored && noTracers && noChildren) {
            if (this.asyncTransactions.remove(txa.getTransaction())) {
                this.mergeAsyncTransactionData(txa);
                if (Agent.LOG.isFinestEnabled()) {
                    final String msg = MessageFormat.format("Async transaction (excluded from trace) finished for {0}: {1} (remaining: {2})", this.transactionActivityRef.get(), txa, this.asyncTransactions);
                    Agent.LOG.finest(msg);
                }
                while (this.asyncTransactions.remove(txa)) {
                    Agent.LOG.log(Level.FINEST, "Removed the transaction again: " + txa);
                }
            }
        }
        else if (this.asyncTransactions.remove(txa.getTransaction()) && this.asyncTransactionActivitiesComplete.add(txa)) {
            if (Agent.LOG.isFinestEnabled()) {
                final String msg = MessageFormat.format("Async transaction finished for {0}: {1} (remaining: {2})", this.transactionActivityRef.get(), txa, this.asyncTransactions);
                Agent.LOG.finest(msg);
            }
            while (this.asyncTransactions.remove(txa)) {
                Agent.LOG.log(Level.FINEST, "Removed the transaction again: " + txa);
            }
        }
        this.tryComplete(true);
    }
    
    public void asyncJobStarted(final TransactionHolder job) {
        if (this.isComplete() || this.invalidateAsyncJobs.get()) {
            return;
        }
        if (!this.asyncJobs.contains(job) && this.asyncJobs.add(job) && Agent.LOG.isFinestEnabled()) {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String msg = MessageFormat.format("Async job started for {0}: {1} ({2})", this.transactionActivityRef.get(), job, Arrays.asList(stackTrace).toString());
            Agent.LOG.finest(msg);
        }
    }
    
    public void asyncJobInvalidate(final TransactionHolder job) {
        if (this.isComplete()) {
            return;
        }
        if (this.asyncJobs.remove(job)) {
            if (Agent.LOG.isFinestEnabled()) {
                final String msg = MessageFormat.format("Async job removed from transaction. {0}: {1} (remaining: {2})", this.transactionActivityRef.get(), job, this.asyncJobs);
                Agent.LOG.finest(msg);
            }
            this.tryComplete(false);
        }
    }
    
    public void asyncJobFinished(final TransactionHolder job) {
        if (this.isComplete()) {
            return;
        }
        if (this.asyncJobs.remove(job)) {
            if (Agent.LOG.isFinestEnabled()) {
                final String msg = MessageFormat.format("Async job finished for {0}: {1} (remaining: {2})", this.transactionActivityRef.get(), job, this.asyncJobs);
                Agent.LOG.finest(msg);
            }
            this.tryComplete(job != TransactionHolderDispatcherPointCut.TRANSACTION_HOLDER);
        }
    }
    
    public void setInvalidateAsyncJobs(final boolean invalidate) {
        this.invalidateAsyncJobs.set(invalidate);
    }
    
    public void printIncompleteTransactionGraph() {
        if (Agent.LOG.isFinerEnabled()) {
            Agent.LOG.finer("Job Graph for " + this.transactionActivityRef.get());
            this.printIncompleteTransactionGraph(0);
        }
    }
    
    private void printIncompleteTransactionGraph(final int indentLevel) {
        if (indentLevel >= 150) {
            Agent.LOG.finer("Async nesting too deep!");
            return;
        }
        final String indent = Strings.repeat("-", indentLevel * 2);
        for (final Transaction tx : this.asyncTransactions) {
            Agent.LOG.finer(indent + "+ Tx: " + tx + ((Transaction.getTransaction() == tx) ? " <!>" : ""));
            if (tx.getTransactionState() instanceof AsyncTransactionState) {
                final AsyncTransactionState transactionState = (AsyncTransactionState)tx.getTransactionState();
                transactionState.printIncompleteTransactionGraph(indentLevel + 1);
            }
        }
        for (final TransactionHolder th : this.asyncJobs) {
            if (th._nr_getTransaction() instanceof Transaction) {
                final Transaction transaction = (Transaction)th._nr_getTransaction();
                Agent.LOG.finer(indent + "> Job: " + th + " (" + transaction + ")");
            }
            else {
                Agent.LOG.finer(indent + "> Th: " + th);
            }
        }
    }
    
    static {
        ASYNC_WAIT_SIG = new ClassMethodSignature("NR_ASYNC_WAIT_CLASS", "NR_ASYNC_WAIT_METHOD", "()V");
        ASYNC_WAIT_FORMAT = new SimpleMetricNameFormat("Async Wait");
        ASYNC_TRACER_ARGS = new Object[] { 176, null };
    }
}

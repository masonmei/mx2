// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.tracers.ClassMethodSignature;
import java.util.Map;
import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.lang.reflect.Method;
import com.newrelic.agent.stats.SimpleStatsEngine;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.SkipTracer;
import com.newrelic.agent.tracers.TransactionActivityInitiator;
import java.util.Collections;
import java.lang.management.ThreadMXBean;
import com.newrelic.agent.trace.TransactionTraceService;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import com.newrelic.agent.service.ServiceFactory;
import java.util.logging.Level;
import com.newrelic.agent.transaction.TransactionCache;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.tracers.Tracer;
import java.util.List;

public class TransactionActivity
{
    public static final int NOT_REPORTED = -1;
    private final List<Tracer> tracers;
    private Tracer rootTracer;
    private Tracer lastTracer;
    private final TransactionStats transactionStats;
    private Transaction transaction;
    private final TransactionCache transactionCache;
    private final String threadName;
    private final long cpuStartTimeInNanos;
    private long totalCpuTimeInNanos;
    private int tracerStartLock;
    private volatile boolean activityIsIgnored;
    private int activityId;
    private Object context;
    private static final ThreadLocal<TransactionActivity> activityHolder;
    private static final Tracer FLYWEIGHT_PLACEHOLDER;
    
    public static void clear() {
        TransactionActivity.activityHolder.remove();
        Agent.LOG.log(Level.FINEST, "TransactionActivity.clear()");
    }
    
    public static void set(final TransactionActivity txa) {
        TransactionActivity.activityHolder.set(txa);
        Agent.LOG.log(Level.FINEST, "TransactionActivity.set({0})", new Object[] { txa });
    }
    
    public static TransactionActivity get() {
        final TransactionActivity result = TransactionActivity.activityHolder.get();
        return result;
    }
    
    public static TransactionActivity create(final Transaction transaction, final int id) {
        final TransactionActivity txa = new TransactionActivity(transaction);
        txa.activityId = id;
        TransactionActivity.activityHolder.set(txa);
        Agent.LOG.log(Level.FINE, "created {0} for {1}", new Object[] { txa, transaction });
        return txa;
    }
    
    private TransactionActivity(final Transaction tx) {
        this.activityIsIgnored = false;
        this.context = null;
        this.transaction = tx;
        final TransactionTraceService ttService = ServiceFactory.getTransactionTraceService();
        this.tracers = (ttService.isEnabled() ? new ArrayList<Tracer>(128) : null);
        this.transactionStats = new TransactionStats();
        this.transactionCache = new TransactionCache();
        final Thread thread = Thread.currentThread();
        this.threadName = thread.getName();
        if (ttService.isEnabled()) {
            if (ttService.isThreadCpuTimeEnabled()) {
                final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
                this.cpuStartTimeInNanos = threadMXBean.getCurrentThreadCpuTime();
                this.totalCpuTimeInNanos = 0L;
            }
            else {
                this.cpuStartTimeInNanos = -1L;
                this.totalCpuTimeInNanos = -1L;
            }
        }
        else {
            this.cpuStartTimeInNanos = -1L;
            this.totalCpuTimeInNanos = -1L;
        }
    }
    
    public TransactionActivity() {
        this.activityIsIgnored = false;
        this.context = null;
        final String realClassName = this.getClass().getSimpleName();
        if (!realClassName.startsWith("Mock")) {
            throw new IllegalStateException("the public constructor is only for test purposes.");
        }
        this.tracers = null;
        this.transactionStats = null;
        this.transactionCache = null;
        this.threadName = "MockThread";
        this.cpuStartTimeInNanos = -1L;
        this.totalCpuTimeInNanos = -1L;
    }
    
    public boolean canCreateTransactionSegment() {
        return this.transaction.shouldGenerateTransactionSegment();
    }
    
    public Object getContext() {
        if (this.context == null) {
            Agent.LOG.log(Level.FINE, "TransactionActivity: context is null.");
        }
        return this.context;
    }
    
    public void setContext(final Object context) {
        if (context == null) {
            Agent.LOG.log(Level.FINE, "TransactionActivity: context is being set to null.");
        }
        this.context = context;
    }
    
    public TransactionStats getTransactionStats() {
        return this.transactionStats;
    }
    
    public List<Tracer> getTracers() {
        return Collections.unmodifiableList((List<? extends Tracer>)this.tracers);
    }
    
    public long getTotalCpuTime() {
        return this.totalCpuTimeInNanos;
    }
    
    public void setToIgnore() {
        this.activityIsIgnored = true;
    }
    
    void setOwningTransactionIsIgnored(final boolean newState) {
        this.activityIsIgnored = newState;
    }
    
    public Tracer tracerStarted(final Tracer tracer) {
        if (this.isTracerStartLocked()) {
            Agent.LOG.log(Level.FINER, "tracerStarted ignored: tracerStartLock is already active");
            return null;
        }
        if (!this.isStarted()) {
            if (!(tracer instanceof TransactionActivityInitiator)) {
                return null;
            }
            this.setRootTracer(tracer);
        }
        else if (tracer.getParentTracer() != null) {
            this.addTracer(this.lastTracer = tracer);
        }
        else if (Agent.LOG.isFinestEnabled()) {
            Agent.LOG.log(Level.FINEST, "tracerStarted: {0} cannot be added: no parent pointer", new Object[] { tracer });
        }
        return tracer;
    }
    
    public void tracerFinished(final Tracer tracer, final int opcode) {
        if (tracer instanceof SkipTracer) {
            return;
        }
        if (tracer != this.lastTracer) {
            this.failed(this, tracer, opcode);
        }
        else if (tracer == this.rootTracer) {
            this.finished(this.rootTracer, opcode);
        }
        else {
            this.lastTracer = tracer.getParentTracer();
        }
    }
    
    private void failed(final TransactionActivity activity, final Tracer tracer, final int opcode) {
        Agent.LOG.log(Level.SEVERE, "Inconsistent state!  tracer != last tracer for {0} ({1} != {2})", new Object[] { this, tracer, this.lastTracer });
        try {
            this.transaction.activityFailed(this, opcode);
        }
        finally {
            TransactionActivity.activityHolder.remove();
        }
    }
    
    private void finished(final Tracer tracer, final int opcode) {
        if (Agent.LOG.isFinestEnabled()) {
            Agent.LOG.log(Level.FINEST, "tracerFinished: {0} opcode: {1} in transactionActivity {2}", new Object[] { tracer, opcode, this });
        }
        try {
            if (!this.activityIsIgnored) {
                this.recordCpu();
            }
            this.transaction.activityFinished(this, tracer, opcode);
        }
        finally {
            TransactionActivity.activityHolder.remove();
        }
    }
    
    public boolean isStarted() {
        return this.rootTracer != null;
    }
    
    public boolean isFlyweight() {
        return this.lastTracer != null && this.lastTracer.isLeaf();
    }
    
    public void recordCpu() {
        if (this.transaction.isTransactionTraceEnabled() && this.cpuStartTimeInNanos > -1L && this.totalCpuTimeInNanos == 0L) {
            this.totalCpuTimeInNanos = ServiceFactory.getTransactionTraceService().getThreadMXBean().getCurrentThreadCpuTime() - this.cpuStartTimeInNanos;
        }
    }
    
    public void addTracer(final Tracer tracer) {
        if (tracer.isTransactionSegment() && this.tracers != null) {
            this.getTransaction().getTransactionCounts().addTracer();
            this.tracers.add(tracer);
        }
    }
    
    private void setRootTracer(final Tracer tracer) {
        this.rootTracer = tracer;
        this.lastTracer = tracer;
        this.transaction.activityStarted(this);
        if (tracer instanceof DefaultTracer) {
            ((DefaultTracer)this.rootTracer).setAttribute("exec_context", this.threadName);
        }
        this.getTransaction().getTransactionCounts().addTracer();
    }
    
    public void lockTracerStart() {
        --this.tracerStartLock;
    }
    
    public void unlockTracerStart() {
        ++this.tracerStartLock;
    }
    
    public boolean isTracerStartLocked() {
        return this.tracerStartLock < 0;
    }
    
    public boolean checkTracerStart() {
        if (this.isTracerStartLocked()) {
            return false;
        }
        if (!this.isFlyweight() && !this.activityIsIgnored) {
            this.lockTracerStart();
            return true;
        }
        return false;
    }
    
    public Tracer getLastTracer() {
        return this.lastTracer;
    }
    
    public TracedMethod startFlyweightTracer() {
        try {
            if (this.rootTracer == null) {
                return null;
            }
            final Tracer tracer = this.lastTracer;
            if (this.lastTracer.isLeaf()) {
                return null;
            }
            this.lastTracer = TransactionActivity.FLYWEIGHT_PLACEHOLDER;
            return (TracedMethod)tracer;
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINEST, t, "Error starting tracer", new Object[0]);
            return null;
        }
    }
    
    public void finishFlyweightTracer(final TracedMethod parent, final long startInNanos, final long finishInNanos, final String className, final String methodName, final String methodDesc, final String metricName, final String[] rollupMetricNames) {
        try {
            if (parent instanceof DefaultTracer) {
                final DefaultTracer parentTracer = (DefaultTracer)parent;
                final long duration = finishInNanos - startInNanos;
                if (this.lastTracer == TransactionActivity.FLYWEIGHT_PLACEHOLDER) {
                    this.lastTracer = parentTracer;
                }
                else {
                    Agent.LOG.log(Level.FINEST, "Error finishing tracer - the last tracer is of the wrong type.");
                }
                if (duration < 0L) {
                    Agent.LOG.log(Level.FINEST, "A tracer finished with a negative duration.");
                    return;
                }
                this.transactionStats.getScopedStats().getResponseTimeStats(metricName).recordResponseTimeInNanos(duration);
                Agent.LOG.log(Level.FINEST, "Finished flyweight tracer {0} ({1}.{2}{3})", new Object[] { metricName, className, methodName, methodDesc });
                if (rollupMetricNames != null) {
                    final SimpleStatsEngine unscopedStats = this.transactionStats.getUnscopedStats();
                    for (final String name : rollupMetricNames) {
                        unscopedStats.getResponseTimeStats(name).recordResponseTimeInNanos(duration);
                    }
                }
                parentTracer.childTracerFinished(duration);
            }
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINEST, t, "Error finishing tracer", new Object[0]);
        }
    }
    
    public void startAsyncActivity(final Object context, final Transaction transaction, final int activityId, final Tracer parentTracer) {
        this.setContext(context);
        this.transaction = transaction;
        this.activityId = activityId;
        if (parentTracer != null) {
            this.rootTracer.setParentTracer(parentTracer);
        }
        else {
            Agent.LOG.log(Level.FINE, "TranactionActivity.startAsyncActivity: parentTracer is null.");
        }
    }
    
    public Tracer getRootTracer() {
        return this.rootTracer;
    }
    
    public TransactionCache getTransactionCache() {
        return this.transactionCache;
    }
    
    public Transaction getTransaction() {
        return this.transaction;
    }
    
    public int hashCode() {
        return this.activityId;
    }
    
    static {
        activityHolder = new ThreadLocal<TransactionActivity>() {
            public TransactionActivity get() {
                return super.get();
            }
            
            public void set(final TransactionActivity value) {
                super.set(value);
            }
            
            public void remove() {
                super.remove();
            }
        };
        FLYWEIGHT_PLACEHOLDER = new Tracer() {
            public void setMetricName(final String... metricNameParts) {
            }
            
            public void setMetricNameFormatInfo(final String metricName, final String transactionSegmentName, final String transactionSegmentUri) {
            }
            
            public TracedMethod getParentTracedMethod() {
                return null;
            }
            
            public void finish(final Throwable throwable) {
            }
            
            public void finish(final int opcode, final Object returnValue) {
            }
            
            public long getDurationInMilliseconds() {
                return 0L;
            }
            
            public long getDuration() {
                return 0L;
            }
            
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                return null;
            }
            
            public boolean isTransactionSegment() {
                return false;
            }
            
            public boolean isParent() {
                return false;
            }
            
            public void setParentTracer(final Tracer tracer) {
            }
            
            public boolean isMetricProducer() {
                return true;
            }
            
            public boolean isChildHasStackTrace() {
                return false;
            }
            
            public String getTransactionSegmentUri() {
                return null;
            }
            
            public String getTransactionSegmentName() {
                return null;
            }
            
            public TransactionSegment getTransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final long startTime, final TransactionSegment lastSibling) {
                return null;
            }
            
            public long getStartTimeInMilliseconds() {
                return 0L;
            }
            
            public long getStartTime() {
                return 0L;
            }
            
            public long getRunningDurationInNanos() {
                return 0L;
            }
            
            public Tracer getParentTracer() {
                return null;
            }
            
            public Map<String, Object> getAttributes() {
                return Collections.emptyMap();
            }
            
            public String getMetricName() {
                return null;
            }
            
            public long getExclusiveDuration() {
                return 0L;
            }
            
            public long getEndTimeInMilliseconds() {
                return 0L;
            }
            
            public long getEndTime() {
                return 0L;
            }
            
            public ClassMethodSignature getClassMethodSignature() {
                return null;
            }
            
            public void childTracerFinished(final Tracer child) {
                throw new UnsupportedOperationException();
            }
            
            public boolean isLeaf() {
                return true;
            }
            
            public void setRollupMetricNames(final String... metricNames) {
            }
            
            public void nameTransaction(final TransactionNamePriority namePriority) {
            }
            
            public void addRollupMetricName(final String... metricNameParts) {
            }
            
            public void addExclusiveRollupMetricName(final String... metricNameParts) {
            }
            
            public void setAttribute(final String key, final Object value) {
            }
            
            public Object getAttribute(final String key) {
                return null;
            }
        };
    }
}

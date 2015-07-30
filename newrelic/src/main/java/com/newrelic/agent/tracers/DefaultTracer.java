// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.database.SqlObfuscator;
import java.util.Iterator;
import com.newrelic.agent.stats.ResponseTimeStats;
import com.newrelic.agent.stats.TransactionStats;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Arrays;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.Transaction;
import java.util.Map;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class DefaultTracer extends AbstractTracer
{
    public static final MetricNameFormat NULL_METRIC_NAME_FORMATTER;
    public static final String BACKTRACE_PARAMETER_NAME = "backtrace";
    protected static final int DEFAULT_TRACER_FLAGS = 6;
    private static final int INITIAL_PARAMETER_MAP_SIZE = 5;
    private final long startTime;
    private long duration;
    private long exclusiveDuration;
    private Map<String, Object> attributes;
    private Tracer parentTracer;
    private final ClassMethodSignature classMethodSignature;
    private Object invocationTarget;
    private MetricNameFormat metricNameFormat;
    private boolean isParent;
    private boolean childHasStackTrace;
    private final byte tracerFlags;
    
    public DefaultTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final MetricNameFormat metricNameFormatter, final int tracerFlags) {
        this(transaction.getTransactionActivity(), sig, object, metricNameFormatter, tracerFlags);
    }
    
    public DefaultTracer(final TransactionActivity txa, final ClassMethodSignature sig, final Object object, final MetricNameFormat metricNameFormatter, int tracerFlags) {
        super(txa);
        this.metricNameFormat = metricNameFormatter;
        this.classMethodSignature = sig;
        this.startTime = System.nanoTime();
        this.invocationTarget = object;
        this.parentTracer = txa.getLastTracer();
        if (!txa.canCreateTransactionSegment()) {
            tracerFlags = TracerFlags.clearSegment(tracerFlags);
        }
        this.tracerFlags = (byte)tracerFlags;
    }
    
    public DefaultTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final MetricNameFormat metricNameFormatter) {
        this(transaction, sig, object, metricNameFormatter, 6);
    }
    
    public DefaultTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object) {
        this(transaction, sig, object, DefaultTracer.NULL_METRIC_NAME_FORMATTER);
    }
    
    public void finish(final Throwable throwable) {
        if (!this.getTransaction().getTransactionState().finish(this.getTransaction(), this)) {
            return;
        }
        try {
            this.getTransactionActivity().lockTracerStart();
            this.doFinish(throwable);
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("An error occurred finishing tracer for class {0} : {1}", this.classMethodSignature.getClassName(), t);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.WARNING, msg, t);
            }
            else {
                Agent.LOG.warning(msg);
            }
        }
        finally {
            this.getTransactionActivity().unlockTracerStart();
        }
        this.finish(191, null);
        if (Agent.isDebugEnabled()) {
            Agent.LOG.log(Level.FINE, "(Debug) Tracer.finish(Throwable)");
        }
    }
    
    protected void reset() {
        this.invocationTarget = null;
    }
    
    public void finish(final int opcode, final Object returnValue) {
        if (!this.getTransaction().getTransactionState().finish(this.getTransaction(), this)) {
            return;
        }
        this.duration = Math.max(0L, System.nanoTime() - this.getStartTime());
        this.exclusiveDuration += this.duration;
        if (this.exclusiveDuration < 0L || this.exclusiveDuration > this.duration) {
            final String msg = MessageFormat.format("Invalid exclusive time {0} for tracer {1}", this.exclusiveDuration, this.getClass().getName());
            Agent.LOG.severe(msg);
            this.exclusiveDuration = this.duration;
        }
        this.getTransactionActivity().lockTracerStart();
        try {
            try {
                if (191 != opcode) {
                    this.doFinish(opcode, returnValue);
                }
            }
            catch (Throwable t) {
                final String msg2 = MessageFormat.format("An error occurred finishing tracer for class {0} : {1}", this.classMethodSignature.getClassName(), t.toString());
                Agent.LOG.severe(msg2);
                Agent.LOG.log(Level.FINER, msg2, t);
            }
            try {
                this.attemptToStoreStackTrace();
            }
            catch (Throwable t) {
                if (Agent.LOG.isFinestEnabled()) {
                    final String msg2 = MessageFormat.format("An error occurred getting stack trace for class {0} : {1}", this.classMethodSignature.getClassName(), t.toString());
                    Agent.LOG.log(Level.FINEST, msg2, t);
                }
            }
            if (this.parentTracer != null) {
                this.parentTracer.childTracerFinished(this);
            }
            try {
                this.recordMetrics(this.getTransactionActivity().getTransactionStats());
            }
            catch (Throwable t) {
                final String msg2 = MessageFormat.format("An error occurred recording tracer metrics for class {0} : {1}", this.classMethodSignature.getClassName(), t.toString());
                Agent.LOG.severe(msg2);
                Agent.LOG.log(Level.FINER, msg2, t);
            }
            try {
                if (!(this instanceof SkipTracer)) {
                    this.getTransactionActivity().tracerFinished(this, opcode);
                }
            }
            catch (Throwable t) {
                final String msg2 = MessageFormat.format("An error occurred calling Transaction.tracerFinished() for class {0} : {1}", this.classMethodSignature.getClassName(), t.toString());
                Agent.LOG.severe(msg2);
                Agent.LOG.log(Level.FINER, msg2, t);
            }
            this.reset();
        }
        finally {
            this.getTransactionActivity().unlockTracerStart();
        }
    }
    
    protected void doFinish(final Throwable throwable) {
    }
    
    protected void doFinish(final int opcode, final Object returnValue) {
    }
    
    protected boolean shouldStoreStackTrace() {
        return this.isTransactionSegment();
    }
    
    private void attemptToStoreStackTrace() {
        if (this.shouldStoreStackTrace()) {
            final TransactionTracerConfig transactionTracerConfig = this.getTransaction().getTransactionTracerConfig();
            final double stackTraceThresholdInNanos = transactionTracerConfig.getStackTraceThresholdInNanos();
            final int stackTraceMax = transactionTracerConfig.getMaxStackTraces();
            if (this.getDuration() > stackTraceThresholdInNanos && (this.childHasStackTrace || this.getTransaction().getTransactionCounts().getStackTraceCount() < stackTraceMax)) {
                this.storeStackTrace();
                if (!this.childHasStackTrace) {
                    this.getTransaction().getTransactionCounts().incrementStackTraceCount();
                    this.childHasStackTrace = true;
                }
            }
        }
    }
    
    public void storeStackTrace() {
        this.setAttribute("backtrace", Thread.currentThread().getStackTrace());
    }
    
    public void setAttribute(final String key, Object value) {
        if (this.getTransaction().getTransactionCounts().isOverTracerSegmentLimit()) {
            return;
        }
        if (value.getClass().isArray()) {
            value = Arrays.asList((Object[])value);
        }
        this.getTransaction().getTransactionCounts().incrementSize(sizeof(value));
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>(1, 5.0f);
        }
        this.attributes.put(key, value);
    }
    
    public Object getAttribute(final String key) {
        return (this.attributes == null) ? null : this.attributes.get(key);
    }
    
    static int sizeof(final Object value) {
        int size = 0;
        if (value == null) {
            return 0;
        }
        if (value instanceof String) {
            return ((String)value).length();
        }
        if (value instanceof StackTraceElement) {
            final StackTraceElement elem = (StackTraceElement)value;
            return sizeof(elem.getClassName()) + sizeof(elem.getFileName()) + sizeof(elem.getMethodName()) + 10;
        }
        if (value instanceof Object[]) {
            for (final Object obj : (Object[])value) {
                size += sizeof(obj);
            }
        }
        return size;
    }
    
    public Map<String, Object> getAttributes() {
        if (this.attributes == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.attributes);
    }
    
    public long getRunningDurationInNanos() {
        return (this.duration > 0L) ? this.duration : Math.max(0L, System.nanoTime() - this.getStartTime());
    }
    
    public long getDurationInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getDuration(), TimeUnit.NANOSECONDS);
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public long getExclusiveDuration() {
        return this.exclusiveDuration;
    }
    
    public long getEndTime() {
        return this.getStartTime() + this.duration;
    }
    
    public long getEndTimeInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getEndTime(), TimeUnit.NANOSECONDS);
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getStartTimeInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getStartTime(), TimeUnit.NANOSECONDS);
    }
    
    protected final Object getInvocationTarget() {
        return this.invocationTarget;
    }
    
    public Tracer getParentTracer() {
        return this.parentTracer;
    }
    
    public void setParentTracer(final Tracer tracer) {
        this.parentTracer = tracer;
    }
    
    public String getRequestMetricName() {
        return null;
    }
    
    public void setMetricNameFormat(final MetricNameFormat nameFormat) {
        this.metricNameFormat = nameFormat;
    }
    
    protected final MetricNameFormat getMetricNameFormat() {
        return this.metricNameFormat;
    }
    
    public final String getMetricName() {
        return (this.metricNameFormat == null) ? null : this.metricNameFormat.getMetricName();
    }
    
    public final String getTransactionSegmentName() {
        return (this.metricNameFormat == null) ? null : this.metricNameFormat.getTransactionSegmentName();
    }
    
    public final String getTransactionSegmentUri() {
        return (this.metricNameFormat == null) ? null : this.metricNameFormat.getTransactionSegmentUri();
    }
    
    protected void recordMetrics(final TransactionStats transactionStats) {
        if (this.getTransaction().isIgnore()) {
            return;
        }
        if (this.isMetricProducer()) {
            final String metricName = this.getMetricName();
            if (metricName != null) {
                final ResponseTimeStats stats = transactionStats.getScopedStats().getResponseTimeStats(metricName);
                stats.recordResponseTimeInNanos(this.getDuration(), this.getExclusiveDuration());
            }
            if (this.getRollupMetricNames() != null) {
                for (final String name : this.getRollupMetricNames()) {
                    final ResponseTimeStats stats2 = transactionStats.getUnscopedStats().getResponseTimeStats(name);
                    stats2.recordResponseTimeInNanos(this.getDuration(), this.getExclusiveDuration());
                }
            }
            if (this.getExclusiveRollupMetricNames() != null) {
                for (final String name : this.getExclusiveRollupMetricNames()) {
                    final ResponseTimeStats stats2 = transactionStats.getUnscopedStats().getResponseTimeStats(name);
                    stats2.recordResponseTimeInNanos(this.getExclusiveDuration(), this.getExclusiveDuration());
                }
            }
            this.doRecordMetrics(transactionStats);
        }
    }
    
    protected void doRecordMetrics(final TransactionStats transactionStats) {
    }
    
    public final boolean isParent() {
        return this.isParent;
    }
    
    public void childTracerFinished(final Tracer child) {
        if (child.isMetricProducer() && !(child instanceof SkipTracer)) {
            this.exclusiveDuration -= child.getDuration();
            if (this.isTransactionSegment() && child.isTransactionSegment()) {
                this.isParent = true;
                if (child.isChildHasStackTrace()) {
                    this.childHasStackTrace = true;
                }
            }
        }
    }
    
    public void childTracerFinished(final long childDurationInNanos) {
        this.exclusiveDuration -= childDurationInNanos;
    }
    
    public ClassMethodSignature getClassMethodSignature() {
        return this.classMethodSignature;
    }
    
    public final boolean isTransactionSegment() {
        return (this.tracerFlags & 0x4) == 0x4;
    }
    
    public boolean isMetricProducer() {
        return (this.tracerFlags & 0x2) == 0x2;
    }
    
    public final boolean isLeaf() {
        return (this.tracerFlags & 0x20) == 0x20;
    }
    
    public boolean isChildHasStackTrace() {
        return this.childHasStackTrace;
    }
    
    public TransactionSegment getTransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final long startTime, final TransactionSegment lastSibling) {
        return new TransactionSegment(ttConfig, sqlObfuscator, startTime, this);
    }
    
    public void setMetricName(final String... metricNameParts) {
        final String metricName = Strings.join('/', metricNameParts);
        this.setMetricNameFormat(new SimpleMetricNameFormat(metricName));
    }
    
    public void setMetricNameFormatInfo(final String metricName, final String transactionSegmentName, final String transactionSegmentUri) {
        final MetricNameFormat format = new SimpleMetricNameFormat(metricName, transactionSegmentName, transactionSegmentUri);
        this.setMetricNameFormat(format);
    }
    
    static {
        NULL_METRIC_NAME_FORMATTER = new SimpleMetricNameFormat(null);
    }
}

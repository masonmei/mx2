// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.async;

import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.util.Strings;
import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.util.Iterator;
import com.newrelic.agent.stats.ResponseTimeStats;
import com.newrelic.agent.stats.TransactionStats;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Arrays;
import com.newrelic.agent.tracers.SkipTracer;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.Tracer;
import java.util.Map;
import com.newrelic.agent.tracers.AbstractTracer;

public class AsyncTracer extends AbstractTracer
{
    private static final int INITIAL_PARAMETER_MAP_SIZE = 5;
    private final long startTime;
    private final long duration;
    private Map<String, Object> attributes;
    private volatile Tracer parentTracer;
    private final ClassMethodSignature classMethodSignature;
    private MetricNameFormat metricNameFormat;
    private final boolean metricProducer;
    private boolean isParent;
    private final TransactionActivity tracerParentActivty;
    
    public AsyncTracer(final TransactionActivity rootTxActivty, final TransactionActivity tracerParentActivty, final ClassMethodSignature sig, final MetricNameFormat metricNameFormatter, final long startTime, final long endTime) {
        super(rootTxActivty);
        this.tracerParentActivty = tracerParentActivty;
        this.startTime = startTime;
        this.metricNameFormat = metricNameFormatter;
        this.classMethodSignature = sig;
        this.parentTracer = rootTxActivty.getLastTracer();
        this.metricProducer = true;
        this.duration = Math.max(0L, endTime - startTime);
    }
    
    public final void finish(final Throwable throwable) {
        final TransactionActivity activity = this.getTransaction().getTransactionActivity();
        try {
            activity.lockTracerStart();
            this.doFinish(throwable);
            activity.unlockTracerStart();
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("An error occurred finishing tracer for class {0} : {1}", this.classMethodSignature.getClassName(), t);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.WARNING, msg, t);
            }
            else {
                Agent.LOG.warning(msg);
            }
            activity.unlockTracerStart();
        }
        finally {
            activity.unlockTracerStart();
        }
        this.finish(191, null);
        if (Agent.isDebugEnabled()) {
            Agent.LOG.log(Level.FINE, "(Debug) Tracer.finish(Throwable)");
        }
    }
    
    public void finish(final int opcode, final Object returnValue) {
        final TransactionActivity activity = this.getTransactionActivity();
        try {
            activity.lockTracerStart();
            if (191 != opcode) {
                this.doFinish(opcode, returnValue);
            }
            activity.unlockTracerStart();
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("An error occurred finishing tracer for class {0} : {1}", this.classMethodSignature.getClassName(), t.toString());
            Agent.LOG.severe(msg);
            Agent.LOG.log(Level.FINER, msg, t);
            activity.unlockTracerStart();
        }
        finally {
            activity.unlockTracerStart();
        }
        if (this.parentTracer != null) {
            this.parentTracer.childTracerFinished(this);
        }
        try {
            this.recordMetrics(this.getTransaction().getTransactionActivity().getTransactionStats());
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("An error occurred recording tracer metrics for class {0} : {1}", this.classMethodSignature.getClassName(), t.toString());
            Agent.LOG.severe(msg);
            Agent.LOG.log(Level.FINER, msg, t);
        }
        try {
            if (!(this instanceof SkipTracer)) {
                activity.tracerFinished(this, opcode);
            }
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("An error occurred calling Transaction.tracerFinished() for class {0} : {1}", this.classMethodSignature.getClassName(), t.toString());
            Agent.LOG.severe(msg);
            Agent.LOG.log(Level.FINER, msg, t);
        }
    }
    
    protected void doFinish(final Throwable throwable) {
    }
    
    protected void doFinish(final int opcode, final Object returnValue) {
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
    
    public void setAttribute(final String key, Object value) {
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
    
    public Map<String, Object> getAttributes() {
        if (this.attributes == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.attributes);
    }
    
    public long getRunningDurationInNanos() {
        return this.duration;
    }
    
    public long getDurationInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getDuration(), TimeUnit.NANOSECONDS);
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public long getExclusiveDuration() {
        return 0L;
    }
    
    public long getEndTime() {
        return this.startTime + this.duration;
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
    
    public Tracer getParentTracer() {
        return this.parentTracer;
    }
    
    public void setParentTracer(final Tracer tracer) {
        this.parentTracer = tracer;
    }
    
    public TransactionActivity getTracerParentActivty() {
        return this.tracerParentActivty;
    }
    
    public String getRequestMetricName() {
        return null;
    }
    
    protected final void setMetricNameFormat(final MetricNameFormat nameFormat) {
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
        this.isParent = (child.isMetricProducer() && child.isTransactionSegment() && !(child instanceof SkipTracer));
    }
    
    public ClassMethodSignature getClassMethodSignature() {
        return this.classMethodSignature;
    }
    
    public boolean isTransactionSegment() {
        return true;
    }
    
    public boolean isMetricProducer() {
        return this.metricProducer;
    }
    
    public TransactionSegment getTransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final long startTime, final TransactionSegment lastSibling) {
        return new TransactionSegment(ttConfig, sqlObfuscator, startTime, this);
    }
    
    public void setMetricName(final String... metricNameParts) {
        final String metricName = Strings.join('/', metricNameParts);
        this.setMetricNameFormat(new SimpleMetricNameFormat(metricName));
    }
    
    public void setMetricNameFormatInfo(final String metricName, final String transactionSegmentName, final String transactionSegmentUri) {
    }
}

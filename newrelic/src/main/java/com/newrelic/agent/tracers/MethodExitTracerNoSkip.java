// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.trace.TransactionSegment;
import com.newrelic.agent.database.SqlObfuscator;
import com.newrelic.agent.config.TransactionTracerConfig;
import java.util.Collections;
import java.util.Map;
import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.Transaction;

public abstract class MethodExitTracerNoSkip extends AbstractTracer
{
    private final ClassMethodSignature signature;
    protected Tracer parentTracer;
    
    protected abstract void doFinish(final int p0, final Object p1);
    
    public MethodExitTracerNoSkip(final ClassMethodSignature signature, final Transaction transaction) {
        super(transaction);
        this.signature = signature;
        this.parentTracer = ((transaction == null) ? null : transaction.getTransactionActivity().getLastTracer());
    }
    
    public MethodExitTracerNoSkip(final ClassMethodSignature signature, final TransactionActivity activity) {
        super(activity);
        this.signature = signature;
        this.parentTracer = ((activity == null) ? null : activity.getLastTracer());
    }
    
    public void childTracerFinished(final Tracer child) {
    }
    
    public final void finish(final int opcode, final Object returnValue) {
        try {
            this.doFinish(opcode, returnValue);
            if (this.getTransaction() != null) {
                this.getTransaction().getTransactionActivity().tracerFinished(this, opcode);
            }
        }
        finally {
            if (this.getTransaction() != null) {
                this.getTransaction().getTransactionActivity().tracerFinished(this, opcode);
            }
        }
    }
    
    public void finish(final Throwable throwable) {
    }
    
    public Tracer getParentTracer() {
        return this.parentTracer;
    }
    
    public void setParentTracer(final Tracer tracer) {
        this.parentTracer = tracer;
    }
    
    public final ClassMethodSignature getClassMethodSignature() {
        return this.signature;
    }
    
    public final long getDurationInMilliseconds() {
        return 0L;
    }
    
    public final long getRunningDurationInNanos() {
        return 0L;
    }
    
    public final long getDuration() {
        return 0L;
    }
    
    public final long getExclusiveDuration() {
        return 0L;
    }
    
    public final long getStartTime() {
        return 0L;
    }
    
    public final long getStartTimeInMilliseconds() {
        return 0L;
    }
    
    public final long getEndTime() {
        return 0L;
    }
    
    public final long getEndTimeInMilliseconds() {
        return 0L;
    }
    
    public final String getMetricName() {
        return null;
    }
    
    public String getTransactionSegmentName() {
        return null;
    }
    
    public String getTransactionSegmentUri() {
        return null;
    }
    
    public final Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }
    
    public Object getAttribute(final String key) {
        return null;
    }
    
    public void setAttribute(final String key, final Object value) {
    }
    
    public final boolean isTransactionSegment() {
        return false;
    }
    
    public final boolean isMetricProducer() {
        return false;
    }
    
    public boolean isParent() {
        return false;
    }
    
    public final TransactionSegment getTransactionSegment(final TransactionTracerConfig ttConfig, final SqlObfuscator sqlObfuscator, final long startTime, final TransactionSegment lastSibling) {
        return new TransactionSegment(ttConfig, sqlObfuscator, startTime, this);
    }
    
    public void setMetricName(final String... metricNameParts) {
    }
    
    public void setMetricNameFormatInfo(final String metricName, final String transactionSegmentName, final String transactionSegmentUri) {
    }
}

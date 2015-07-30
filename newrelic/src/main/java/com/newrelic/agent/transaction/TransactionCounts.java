// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.config.AgentConfig;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionCounts
{
    private static final int APPROX_TRACER_SIZE = 128;
    private final int maxTransactionSize;
    private final int maxSegments;
    private final AtomicInteger transactionSize;
    private final AtomicInteger segmentCount;
    private final AtomicInteger explainPlanCount;
    private final AtomicInteger stackTraceCount;
    private volatile boolean overSegmentLimit;
    
    public TransactionCounts(final AgentConfig config) {
        this.transactionSize = new AtomicInteger(0);
        this.segmentCount = new AtomicInteger(0);
        this.explainPlanCount = new AtomicInteger(0);
        this.stackTraceCount = new AtomicInteger(0);
        this.maxSegments = config.getTransactionTracerConfig().getMaxSegments();
        this.maxTransactionSize = config.getTransactionSizeLimit();
    }
    
    public void incrementSize(final int size) {
        this.transactionSize.addAndGet(size);
    }
    
    public int getTransactionSize() {
        return this.transactionSize.intValue();
    }
    
    public void addTracer() {
        final int count = this.segmentCount.incrementAndGet();
        this.transactionSize.addAndGet(128);
        this.overSegmentLimit = (count > this.maxSegments);
    }
    
    public boolean isOverTracerSegmentLimit() {
        return this.overSegmentLimit;
    }
    
    public int getSegmentCount() {
        return this.segmentCount.get();
    }
    
    public boolean isOverTransactionSize() {
        return this.transactionSize.intValue() > this.maxTransactionSize;
    }
    
    public boolean shouldGenerateTransactionSegment() {
        return !this.isOverTracerSegmentLimit() && !this.isOverTransactionSize();
    }
    
    public void incrementStackTraceCount() {
        this.stackTraceCount.incrementAndGet();
    }
    
    public int getStackTraceCount() {
        return this.stackTraceCount.intValue();
    }
    
    public int getExplainPlanCount() {
        return this.explainPlanCount.intValue();
    }
    
    public void incrementExplainPlanCountAndLogIfReachedMax(final int max) {
        final int updatedVal = this.explainPlanCount.incrementAndGet();
        if (updatedVal == max) {
            Agent.LOG.log(Level.FINER, "Reached the maximum number of explain plans.");
        }
    }
}

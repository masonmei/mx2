// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionTimer
{
    private final long startTimeNs;
    private final AtomicLong totalTimeNs;
    private volatile long responseTimeNs;
    private long endTimeNs;
    
    public TransactionTimer(final long startTimeNs) {
        this.startTimeNs = startTimeNs;
        this.totalTimeNs = new AtomicLong(0L);
    }
    
    public void setTransactionEndTimeIfLonger(final long newEndTime) {
        if (newEndTime > this.endTimeNs) {
            this.endTimeNs = newEndTime;
            this.responseTimeNs = this.endTimeNs - this.startTimeNs;
        }
    }
    
    public void incrementTransactionTotalTime(final long rootTracerTimeNs) {
        this.totalTimeNs.addAndGet(rootTracerTimeNs);
    }
    
    public long getResponseTime() {
        return this.responseTimeNs;
    }
    
    public long getRunningDurationInNanos() {
        return (this.responseTimeNs > 0L) ? this.responseTimeNs : Math.max(0L, System.nanoTime() - this.getStartTime());
    }
    
    public long getTotalTime() {
        return this.totalTimeNs.longValue();
    }
    
    public long getStartTime() {
        return this.startTimeNs;
    }
    
    public long getEndTime() {
        return this.endTimeNs;
    }
    
    public long getStartTimeInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getStartTime(), TimeUnit.NANOSECONDS);
    }
    
    public long getResponseTimeInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getResponseTime(), TimeUnit.NANOSECONDS);
    }
    
    public long getTotalTimeInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getTotalTime(), TimeUnit.NANOSECONDS);
    }
    
    public long getTEndTimeInMilliseconds() {
        return TimeUnit.MILLISECONDS.convert(this.getEndTime(), TimeUnit.NANOSECONDS);
    }
}

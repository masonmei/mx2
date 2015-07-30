// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.concurrent.TimeUnit;

public class ResponseTimeStatsImpl extends AbstractStats implements ResponseTimeStats
{
    private static final long NANOSECONDS_PER_SECOND_SQUARED = 1000000000000000000L;
    private long total;
    private long totalExclusive;
    private long minValue;
    private long maxValue;
    private double sumOfSquares;
    
    public Object clone() throws CloneNotSupportedException {
        final ResponseTimeStatsImpl newStats = new ResponseTimeStatsImpl();
        newStats.count = this.count;
        newStats.total = this.total;
        newStats.totalExclusive = this.totalExclusive;
        newStats.minValue = this.minValue;
        newStats.maxValue = this.maxValue;
        newStats.sumOfSquares = this.sumOfSquares;
        return newStats;
    }
    
    public void recordResponseTime(final long responseTime, final TimeUnit timeUnit) {
        final long responseTimeInNanos = TimeUnit.NANOSECONDS.convert(responseTime, timeUnit);
        this.recordResponseTimeInNanos(responseTimeInNanos, responseTimeInNanos);
    }
    
    public void recordResponseTime(final long responseTime, final long exclusiveTime, final TimeUnit timeUnit) {
        final long responseTimeInNanos = TimeUnit.NANOSECONDS.convert(responseTime, timeUnit);
        final long exclusiveTimeInNanos = TimeUnit.NANOSECONDS.convert(exclusiveTime, timeUnit);
        this.recordResponseTimeInNanos(responseTimeInNanos, exclusiveTimeInNanos);
    }
    
    public void recordResponseTimeInNanos(final long responseTime) {
        this.recordResponseTimeInNanos(responseTime, responseTime);
    }
    
    public void recordResponseTimeInNanos(final long responseTime, final long exclusiveTime) {
        double responseTimeAsDouble = responseTime;
        responseTimeAsDouble *= responseTimeAsDouble;
        this.sumOfSquares += responseTimeAsDouble;
        if (this.count > 0) {
            this.minValue = Math.min(responseTime, this.minValue);
        }
        else {
            this.minValue = responseTime;
        }
        ++this.count;
        this.total += responseTime;
        this.maxValue = Math.max(responseTime, this.maxValue);
        this.totalExclusive += exclusiveTime;
    }
    
    public boolean hasData() {
        return this.count > 0 || this.total > 0L || this.totalExclusive > 0L;
    }
    
    public void reset() {
        this.count = 0;
        final long n = 0L;
        this.maxValue = n;
        this.minValue = n;
        this.totalExclusive = n;
        this.total = n;
        this.sumOfSquares = 0.0;
    }
    
    public float getTotal() {
        return this.total / 1.0E9f;
    }
    
    public float getTotalExclusiveTime() {
        return this.totalExclusive / 1.0E9f;
    }
    
    public float getMaxCallTime() {
        return this.maxValue / 1.0E9f;
    }
    
    public float getMinCallTime() {
        return this.minValue / 1.0E9f;
    }
    
    public double getSumOfSquares() {
        return this.sumOfSquares / 1.0E18;
    }
    
    public final void merge(final StatsBase statsObj) {
        if (statsObj instanceof ResponseTimeStatsImpl) {
            final ResponseTimeStatsImpl stats = (ResponseTimeStatsImpl)statsObj;
            if (stats.count > 0) {
                if (this.count > 0) {
                    this.minValue = Math.min(this.minValue, stats.minValue);
                }
                else {
                    this.minValue = stats.minValue;
                }
            }
            this.count += stats.count;
            this.total += stats.total;
            this.totalExclusive += stats.totalExclusive;
            this.maxValue = Math.max(this.maxValue, stats.maxValue);
            this.sumOfSquares += stats.sumOfSquares;
        }
    }
    
    public void recordResponseTime(final int count, final long totalTime, final long minTime, final long maxTime, final TimeUnit unit) {
        final long totalTimeInNanos = TimeUnit.NANOSECONDS.convert(totalTime, unit);
        this.count = count;
        this.total = totalTimeInNanos;
        this.totalExclusive = totalTimeInNanos;
        this.minValue = TimeUnit.NANOSECONDS.convert(minTime, unit);
        this.maxValue = TimeUnit.NANOSECONDS.convert(maxTime, unit);
        double totalTimeInNanosAsDouble = totalTimeInNanos;
        totalTimeInNanosAsDouble *= totalTimeInNanosAsDouble;
        this.sumOfSquares += totalTimeInNanosAsDouble;
    }
    
    public String toString() {
        return "ResponseTimeStatsImpl [total=" + this.total + ", totalExclusive=" + this.totalExclusive + ", minValue=" + this.minValue + ", maxValue=" + this.maxValue + ", sumOfSquares=" + this.sumOfSquares + "]";
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.concurrent.TimeUnit;

public class StatsWorks
{
    public static StatsWork getIncrementCounterWork(final String name, final int count) {
        return new IncrementCounter(name, count);
    }
    
    public static StatsWork getRecordMetricWork(final String name, final float value) {
        return new RecordMetric(name, value);
    }
    
    public static StatsWork getRecordResponseTimeWork(final String name, final long millis) {
        return new RecordResponseTimeMetric(millis, name, TimeUnit.MILLISECONDS);
    }
}

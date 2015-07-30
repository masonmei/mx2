// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import java.util.concurrent.TimeUnit;
import com.newrelic.api.agent.MetricAggregator;

public class NoOpMetricAggregator implements MetricAggregator
{
    public static final MetricAggregator INSTANCE;
    
    public void recordResponseTimeMetric(final String name, final long totalTime, final long exclusiveTime, final TimeUnit timeUnit) {
    }
    
    public void recordMetric(final String name, final float value) {
    }
    
    public void recordResponseTimeMetric(final String name, final long millis) {
    }
    
    public void incrementCounter(final String name) {
    }
    
    public void incrementCounter(final String name, final int count) {
    }
    
    static {
        INSTANCE = (MetricAggregator)new NoOpMetricAggregator();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

public final class NoOpTracedMethod implements TracedMethod
{
    public static final TracedMethod INSTANCE;
    
    public void setMetricName(final String... metricNameParts) {
    }
    
    public void nameTransaction(final TransactionNamePriority namePriority) {
    }
    
    public TracedMethod getParentTracedMethod() {
        return null;
    }
    
    public String getMetricName() {
        return "NoOpTracedMethod";
    }
    
    public void setRollupMetricNames(final String... metricNames) {
    }
    
    public void addRollupMetricName(final String... metricNameParts) {
    }
    
    public void addExclusiveRollupMetricName(final String... metricNameParts) {
    }
    
    public void setMetricNameFormatInfo(final String metricName, final String transactionSegmentName, final String transactionSegmentUri) {
    }
    
    static {
        INSTANCE = new NoOpTracedMethod();
    }
}

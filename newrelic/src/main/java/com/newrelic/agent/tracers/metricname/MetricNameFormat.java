// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.metricname;

public interface MetricNameFormat
{
    String getMetricName();
    
    String getTransactionSegmentName();
    
    String getTransactionSegmentUri();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.metricname;

public abstract class AbstractMetricNameFormat implements MetricNameFormat
{
    public String getTransactionSegmentName() {
        return this.getMetricName();
    }
    
    public String getTransactionSegmentUri() {
        return "";
    }
}

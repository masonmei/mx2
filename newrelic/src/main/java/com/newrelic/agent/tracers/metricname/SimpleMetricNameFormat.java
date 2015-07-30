// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers.metricname;

public class SimpleMetricNameFormat implements MetricNameFormat
{
    private final String metricName;
    private final String transactionSegmentName;
    private final String transactionSegmentUri;
    
    public SimpleMetricNameFormat(final String metricName) {
        this(metricName, metricName, null);
    }
    
    public SimpleMetricNameFormat(final String metricName, final String transactionSegmentName) {
        this(metricName, transactionSegmentName, null);
    }
    
    public SimpleMetricNameFormat(final String metricName, final String transactionSegmentName, final String transactionSegmentUri) {
        this.metricName = metricName;
        this.transactionSegmentName = transactionSegmentName;
        this.transactionSegmentUri = transactionSegmentUri;
    }
    
    public final String getMetricName() {
        return this.metricName;
    }
    
    public String getTransactionSegmentName() {
        return this.transactionSegmentName;
    }
    
    public String getTransactionSegmentUri() {
        return this.transactionSegmentUri;
    }
}

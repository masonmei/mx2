// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.util.Strings;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;

public class MetricNameFormatWithHost implements MetricNameFormat
{
    private final String host;
    private final String metricName;
    
    private MetricNameFormatWithHost(final String host, final String library) {
        this.host = host;
        this.metricName = Strings.join('/', "External", host, library);
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getMetricName() {
        return this.metricName;
    }
    
    public String getTransactionSegmentName() {
        return this.metricName;
    }
    
    public String getTransactionSegmentUri() {
        return null;
    }
    
    public static MetricNameFormatWithHost create(final String host, final String library) {
        return new MetricNameFormatWithHost(host, library);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.metric;

import java.util.HashMap;
import java.util.Map;

public class MetricIdRegistry
{
    public static final int METRIC_LIMIT;
    private static final int INITIAL_CAPACITY = 1000;
    private final Map<MetricName, Integer> metricIds;
    
    public MetricIdRegistry() {
        this.metricIds = new HashMap<MetricName, Integer>(1000);
    }
    
    public Integer getMetricId(final MetricName metricName) {
        return this.metricIds.get(metricName);
    }
    
    public void setMetricId(final MetricName metricName, final Integer metricId) {
        if (this.metricIds.size() == MetricIdRegistry.METRIC_LIMIT) {
            this.metricIds.clear();
        }
        this.metricIds.put(metricName, metricId);
    }
    
    public void clear() {
        this.metricIds.clear();
    }
    
    public int getSize() {
        return this.metricIds.size();
    }
    
    static {
        final String property = System.getProperty("newrelic.metric_registry_limit");
        METRIC_LIMIT = ((null != property) ? Integer.parseInt(property) : 15000);
    }
}

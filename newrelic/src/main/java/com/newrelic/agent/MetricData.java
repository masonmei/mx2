// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.io.IOException;
import java.util.List;
import com.newrelic.agent.deps.org.json.simple.JSONArray;
import java.util.ArrayList;
import java.io.Writer;
import com.newrelic.agent.stats.StatsBase;
import com.newrelic.agent.metric.MetricName;
import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public class MetricData implements JSONStreamAware
{
    private final MetricName metricName;
    private final Integer metricId;
    private final StatsBase stats;
    
    private MetricData(final MetricName metricName, final Integer metricId, final StatsBase stats) {
        this.stats = stats;
        this.metricId = metricId;
        this.metricName = metricName;
    }
    
    public StatsBase getStats() {
        return this.stats;
    }
    
    public MetricName getMetricName() {
        return this.metricName;
    }
    
    public Integer getMetricId() {
        return this.metricId;
    }
    
    public Object getKey() {
        return (this.metricId != null) ? this.metricId : this.metricName;
    }
    
    public String toString() {
        return this.metricName.toString();
    }
    
    public void writeJSONString(final Writer writer) throws IOException {
        final List<Object> result = new ArrayList<Object>(2);
        if (this.metricId == null) {
            result.add(this.metricName);
        }
        else {
            result.add(this.metricId);
        }
        result.add(this.stats);
        JSONArray.writeJSONString(result, writer);
    }
    
    public static MetricData create(final MetricName metricName, final StatsBase stats) {
        return create(metricName, null, stats);
    }
    
    public static MetricData create(final MetricName metricName, final Integer metricId, final StatsBase stats) {
        return new MetricData(metricName, metricId, stats);
    }
}

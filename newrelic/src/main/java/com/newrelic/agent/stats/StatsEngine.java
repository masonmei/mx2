// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import com.newrelic.agent.MetricData;
import com.newrelic.agent.metric.MetricIdRegistry;
import com.newrelic.agent.normalization.Normalizer;
import java.util.List;
import com.newrelic.agent.metric.MetricName;

public interface StatsEngine
{
    Stats getStats(String p0);
    
    Stats getStats(MetricName p0);
    
    void recordEmptyStats(String p0);
    
    void recordEmptyStats(MetricName p0);
    
    ResponseTimeStats getResponseTimeStats(String p0);
    
    ResponseTimeStats getResponseTimeStats(MetricName p0);
    
    ApdexStats getApdexStats(MetricName p0);
    
    List<MetricName> getMetricNames();
    
    void clear();
    
    List<MetricData> getMetricData(Normalizer p0, MetricIdRegistry p1);
    
    void mergeStats(StatsEngine p0);
    
    void mergeStatsResolvingScope(TransactionStats p0, String p1);
    
    int getSize();
}

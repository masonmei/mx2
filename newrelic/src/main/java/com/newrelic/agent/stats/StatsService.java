// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import com.newrelic.api.agent.MetricAggregator;
import com.newrelic.agent.service.Service;

public interface StatsService extends Service
{
    void doStatsWork(StatsWork p0);
    
    StatsEngine getStatsEngineForHarvest(String p0);
    
    MetricAggregator getMetricAggregator();
}

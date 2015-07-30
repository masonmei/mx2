// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service;

import com.newrelic.agent.stats.StatsWorks;
import com.newrelic.agent.stats.StatsWork;
import com.newrelic.agent.stats.RecordResponseTimeMetric;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.stats.StatsService;
import com.newrelic.agent.stats.AbstractMetricAggregator;

public class StatsServiceMetricAggregator extends AbstractMetricAggregator
{
    private final StatsService statsService;
    
    public StatsServiceMetricAggregator(final StatsService statsService) {
        this.statsService = statsService;
    }
    
    protected void doRecordResponseTimeMetric(final String name, final long totalTime, final long exclusiveTime, final TimeUnit timeUnit) {
        this.statsService.doStatsWork(new RecordResponseTimeMetric(totalTime, exclusiveTime, name, timeUnit));
    }
    
    protected void doRecordMetric(final String name, final float value) {
        this.statsService.doStatsWork(StatsWorks.getRecordMetricWork(name, value));
    }
    
    protected void doIncrementCounter(final String name, final int count) {
        this.statsService.doStatsWork(StatsWorks.getIncrementCounterWork(name, count));
    }
}

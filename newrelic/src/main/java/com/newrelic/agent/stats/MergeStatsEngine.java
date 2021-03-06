// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

public class MergeStatsEngine implements StatsWork
{
    private final String appName;
    private final StatsEngine statsEngine;
    
    public MergeStatsEngine(final String appName, final StatsEngine statsEngine) {
        this.appName = appName;
        this.statsEngine = statsEngine;
    }
    
    public void doWork(final StatsEngine statsEngine) {
        statsEngine.mergeStats(this.statsEngine);
    }
    
    public String getAppName() {
        return this.appName;
    }
}

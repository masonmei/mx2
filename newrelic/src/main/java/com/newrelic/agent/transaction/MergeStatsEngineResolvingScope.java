// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.transaction;

import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.stats.TransactionStats;
import com.newrelic.agent.stats.StatsWork;

public class MergeStatsEngineResolvingScope implements StatsWork
{
    private final String appName;
    private final TransactionStats statsEngine;
    private final String resolvedScope;
    
    public MergeStatsEngineResolvingScope(final String resolvedScope, final String appName, final TransactionStats statsEngine) {
        this.resolvedScope = resolvedScope;
        this.appName = appName;
        this.statsEngine = statsEngine;
    }
    
    public void doWork(final StatsEngine statsEngine) {
        statsEngine.mergeStatsResolvingScope(this.statsEngine, this.resolvedScope);
    }
    
    public String getAppName() {
        return this.appName;
    }
}

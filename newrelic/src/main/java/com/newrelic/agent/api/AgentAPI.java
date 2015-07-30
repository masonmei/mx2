// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.api;

import com.newrelic.agent.stats.Stats;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.stats.StatsWork;
import com.newrelic.agent.service.ServiceFactory;

@Deprecated
public class AgentAPI
{
    @Deprecated
    public static void recordValue(final String name, final float value) {
        ServiceFactory.getStatsService().doStatsWork(new StatsWork() {
            public String getAppName() {
                return null;
            }
            
            public void doWork(final StatsEngine statsEngine) {
                statsEngine.getStats(name).recordDataPoint(value);
            }
        });
    }
    
    @Deprecated
    public static Stats getStats(final String name) {
        final String msg = "AgentAPI#getStats(String) is not supported. Use com.newrelic.api.agent.NewRelic";
        throw new UnsupportedOperationException(msg);
    }
}

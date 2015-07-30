// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.HashMap;
import java.util.Map;

public class MonotonicallyIncreasingStatsEngine
{
    private final Map<String, MonotonicallyIncreasingStatsHelper> monoStatsHelpers;
    
    public MonotonicallyIncreasingStatsEngine() {
        this.monoStatsHelpers = new HashMap<String, MonotonicallyIncreasingStatsHelper>();
    }
    
    public void recordMonoStats(final StatsEngine statsEngine, final String name, final float value) {
        final MonotonicallyIncreasingStatsHelper monoStatsHelper = this.getMonotonicallyIncreasingStatsHelper(name);
        final Stats stats = statsEngine.getStats(name);
        monoStatsHelper.recordDataPoint(stats, value);
    }
    
    private MonotonicallyIncreasingStatsHelper getMonotonicallyIncreasingStatsHelper(final String name) {
        MonotonicallyIncreasingStatsHelper monoStatsHelper = this.monoStatsHelpers.get(name);
        if (monoStatsHelper == null) {
            monoStatsHelper = new MonotonicallyIncreasingStatsHelper();
            this.monoStatsHelpers.put(name, monoStatsHelper);
        }
        return monoStatsHelper;
    }
    
    private class MonotonicallyIncreasingStatsHelper
    {
        private float lastValue;
        
        public MonotonicallyIncreasingStatsHelper() {
            this.lastValue = 0.0f;
        }
        
        public void recordDataPoint(final Stats stats, final float value) {
            if (this.lastValue > value) {
                this.lastValue = 0.0f;
            }
            stats.recordDataPoint(value - this.lastValue);
            this.lastValue = value;
        }
    }
}

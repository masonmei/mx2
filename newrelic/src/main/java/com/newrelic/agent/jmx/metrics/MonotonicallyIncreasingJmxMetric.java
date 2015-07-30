// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.jmx.JmxType;
import com.newrelic.agent.stats.MonotonicallyIncreasingStatsEngine;

public class MonotonicallyIncreasingJmxMetric extends JmxMetric
{
    private static final MonotonicallyIncreasingStatsEngine monoStatsEngine;
    
    public MonotonicallyIncreasingJmxMetric(final String attribute) {
        super(attribute);
    }
    
    public MonotonicallyIncreasingJmxMetric(final String[] attributes, final String attName, final JmxAction pAction) {
        super(attributes, attName, pAction);
    }
    
    public JmxType getType() {
        return JmxType.MONOTONICALLY_INCREASING;
    }
    
    public void recordStats(final StatsEngine statsEngine, final String name, final float value) {
        MonotonicallyIncreasingJmxMetric.monoStatsEngine.recordMonoStats(statsEngine, name, value);
    }
    
    static {
        monoStatsEngine = new MonotonicallyIncreasingStatsEngine();
    }
}

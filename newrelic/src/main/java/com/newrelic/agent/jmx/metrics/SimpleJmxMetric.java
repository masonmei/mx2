// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.jmx.JmxType;

public class SimpleJmxMetric extends JmxMetric
{
    public SimpleJmxMetric(final String attribute) {
        super(attribute);
    }
    
    public SimpleJmxMetric(final String[] attributes, final String attName, final JmxAction pAction) {
        super(attributes, attName, pAction);
    }
    
    public JmxType getType() {
        return JmxType.SIMPLE;
    }
    
    public void recordStats(final StatsEngine statsEngine, final String metricName, final float value) {
        statsEngine.getStats(metricName).recordDataPoint(value);
    }
}

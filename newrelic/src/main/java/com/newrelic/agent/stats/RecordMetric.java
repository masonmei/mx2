// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

final class RecordMetric implements StatsWork
{
    private final String name;
    private final float value;
    
    public RecordMetric(final String name, final float value) {
        this.name = name;
        this.value = value;
    }
    
    public void doWork(final StatsEngine statsEngine) {
        statsEngine.getStats(this.name).recordDataPoint(this.value);
    }
    
    public String getAppName() {
        return null;
    }
}

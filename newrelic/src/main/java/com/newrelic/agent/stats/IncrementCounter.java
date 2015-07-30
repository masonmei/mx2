// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

final class IncrementCounter implements StatsWork
{
    private final String name;
    private final int count;
    
    public IncrementCounter(final String name, final int count) {
        this.name = name;
        this.count = count;
    }
    
    public void doWork(final StatsEngine statsEngine) {
        statsEngine.getStats(this.name).incrementCallCount(this.count);
    }
    
    public String getAppName() {
        return null;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

public class TransactionStats
{
    private final SimpleStatsEngine unscopedStats;
    private final SimpleStatsEngine scopedStats;
    
    public TransactionStats() {
        this.unscopedStats = new SimpleStatsEngine(16);
        this.scopedStats = new SimpleStatsEngine();
    }
    
    public SimpleStatsEngine getUnscopedStats() {
        return this.unscopedStats;
    }
    
    public SimpleStatsEngine getScopedStats() {
        return this.scopedStats;
    }
    
    public int getSize() {
        return this.unscopedStats.getStatsMap().size() + this.scopedStats.getStatsMap().size();
    }
    
    public String toString() {
        return "TransactionStats [unscopedStats=" + this.unscopedStats + ", scopedStats=" + this.scopedStats + "]";
    }
}

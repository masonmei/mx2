// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

public interface CountStats extends StatsBase
{
    void incrementCallCount();
    
    void incrementCallCount(int p0);
    
    int getCallCount();
    
    void setCallCount(int p0);
    
    float getTotal();
    
    float getTotalExclusiveTime();
    
    float getMinCallTime();
    
    float getMaxCallTime();
    
    double getSumOfSquares();
}

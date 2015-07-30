// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

public interface ApdexStats extends StatsBase
{
    void recordApdexFrustrated();
    
    void recordApdexResponseTime(long p0, long p1);
    
    int getApdexSatisfying();
    
    int getApdexTolerating();
    
    int getApdexFrustrating();
}

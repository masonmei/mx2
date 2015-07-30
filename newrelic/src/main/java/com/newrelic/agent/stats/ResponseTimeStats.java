// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import java.util.concurrent.TimeUnit;

public interface ResponseTimeStats extends CountStats
{
    void recordResponseTime(long p0, TimeUnit p1);
    
    void recordResponseTime(long p0, long p1, TimeUnit p2);
    
    void recordResponseTime(int p0, long p1, long p2, long p3, TimeUnit p4);
    
    void recordResponseTimeInNanos(long p0, long p1);
    
    void recordResponseTimeInNanos(long p0);
}

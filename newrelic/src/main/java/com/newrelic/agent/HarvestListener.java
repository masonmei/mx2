// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.stats.StatsEngine;

public interface HarvestListener
{
    void beforeHarvest(String p0, StatsEngine p1);
    
    void afterHarvest(String p0);
}

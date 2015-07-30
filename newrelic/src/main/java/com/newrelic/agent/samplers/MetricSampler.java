// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.samplers;

import com.newrelic.agent.stats.StatsEngine;

public interface MetricSampler
{
    void sample(StatsEngine p0);
}

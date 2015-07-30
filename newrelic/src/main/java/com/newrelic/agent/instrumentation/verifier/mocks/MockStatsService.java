// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.verifier.mocks;

import com.newrelic.api.agent.MetricAggregator;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.stats.StatsWork;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.stats.StatsService;

public class MockStatsService implements StatsService
{
    public String getName() {
        return null;
    }
    
    public void start() throws Exception {
    }
    
    public void stop() throws Exception {
    }
    
    public boolean isEnabled() {
        return false;
    }
    
    public IAgentLogger getLogger() {
        return null;
    }
    
    public boolean isStarted() {
        return false;
    }
    
    public boolean isStopped() {
        return false;
    }
    
    public boolean isStartedOrStarting() {
        return false;
    }
    
    public boolean isStoppedOrStopping() {
        return false;
    }
    
    public void doStatsWork(final StatsWork statsWork) {
    }
    
    public StatsEngine getStatsEngineForHarvest(final String appName) {
        return null;
    }
    
    public MetricAggregator getMetricAggregator() {
        return null;
    }
}

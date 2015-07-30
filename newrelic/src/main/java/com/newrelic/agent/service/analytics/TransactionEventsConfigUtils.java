// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import com.newrelic.agent.Agent;
import com.newrelic.agent.config.AgentConfig;

abstract class TransactionEventsConfigUtils
{
    public static final int DEFAULT_MAX_SAMPLES_STORED = 2000;
    public static final boolean DEFAULT_ENABLED = true;
    
    static boolean isTransactionEventsEnabled(final AgentConfig config, final int maxSamplesStored) {
        return maxSamplesStored > 0 && config.getValue("analytics_events.enabled", true) && config.getValue("transaction_events.enabled", true)
                && config.getValue("transaction_events.collect_analytics_events", true);
    }
    
    static int getMaxSamplesStored(final AgentConfig config) {
        final Integer newMax = config.getValue("transaction_events.max_samples_stored");
        if (newMax != null) {
            return newMax;
        }
        final Integer oldMax = config.getValue("analytics_events.max_samples_stored", 2000);
        if (oldMax != null) {
            Agent.LOG.info("The property analytics_events.max_samples_stored is deprecated. Please use transaction_events.max_samples_stored.");
            return oldMax;
        }
        return 2000;
    }
}

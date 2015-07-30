// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.config.AgentConfig;

abstract class CustomInsightsEventsConfigUtils
{
    public static final int DEFAULT_MAX_SAMPLES_STORED = 10000;
    public static final boolean DEFAULT_ENABLED = true;
    
    static boolean isCustomInsightsEventsEnabled(final AgentConfig config, final int maxSamplesStored) {
        final boolean notHighSecurity = !ServiceFactory.getConfigService().getDefaultAgentConfig().isHighSecurity();
        final boolean storedMoreThan0 = maxSamplesStored > 0;
        final Boolean configEnabled = (Boolean)config.getValue("custom_insights_events.enabled", (Object)true);
        final Boolean featureGateEnabled = (Boolean)config.getValue("custom_insights_events.collect_custom_events", (Object)true);
        return notHighSecurity && storedMoreThan0 && configEnabled && featureGateEnabled;
    }
    
    static int getMaxSamplesStored(final AgentConfig config) {
        return (int)config.getValue("custom_insights_events.max_samples_stored", (Object)10000);
    }
}

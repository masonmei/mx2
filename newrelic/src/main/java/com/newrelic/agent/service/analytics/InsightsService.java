// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service.analytics;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.api.agent.Insights;
import com.newrelic.agent.service.Service;

public interface InsightsService extends Service, Insights
{
    Insights getTransactionInsights(AgentConfig p0);
    
    void storeEvent(String p0, CustomInsightsEvent p1);
}

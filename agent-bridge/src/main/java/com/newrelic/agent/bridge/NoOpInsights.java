// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import java.util.Map;
import com.newrelic.api.agent.Insights;

class NoOpInsights implements Insights
{
    static final Insights INSTANCE;
    
    public void recordCustomEvent(final String eventType, final Map<String, Object> attributes) {
    }
    
    static {
        INSTANCE = (Insights)new NoOpInsights();
    }
}

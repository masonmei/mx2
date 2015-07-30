// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.service.Service;

public interface IAgent extends Service
{
    InstrumentationProxy getInstrumentation();
    
    void shutdownAsync();
    
    void shutdown();
}

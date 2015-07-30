// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.Agent;

public interface Agent extends com.newrelic.api.agent.Agent
{
    TracedMethod getTracedMethod();
    
    Transaction getTransaction();
}

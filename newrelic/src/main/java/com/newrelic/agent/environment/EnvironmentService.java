// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.environment;

import com.newrelic.agent.service.Service;

public interface EnvironmentService extends Service
{
    int getProcessPID();
    
    Environment getEnvironment();
}

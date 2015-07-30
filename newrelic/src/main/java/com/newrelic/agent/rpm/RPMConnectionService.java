// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.rpm;

import com.newrelic.agent.IRPMService;
import com.newrelic.agent.service.Service;

public interface RPMConnectionService extends Service
{
    void connect(IRPMService p0);
    
    void connectImmediate(IRPMService p0);
}

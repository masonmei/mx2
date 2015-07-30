// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.List;
import com.newrelic.agent.application.PriorityApplicationName;
import com.newrelic.agent.service.Service;

public interface RPMServiceManager extends Service
{
    void addConnectionListener(ConnectionListener p0);
    
    void removeConnectionListener(ConnectionListener p0);
    
    IRPMService getRPMService();
    
    IRPMService getRPMService(String p0);
    
    IRPMService getOrCreateRPMService(String p0);
    
    IRPMService getOrCreateRPMService(PriorityApplicationName p0);
    
    List<IRPMService> getRPMServices();
}

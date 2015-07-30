// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.service.Service;

public interface HarvestService extends Service
{
    void startHarvest(IRPMService p0);
    
    void addHarvestListener(HarvestListener p0);
    
    void removeHarvestListener(HarvestListener p0);
    
    void harvestNow();
}

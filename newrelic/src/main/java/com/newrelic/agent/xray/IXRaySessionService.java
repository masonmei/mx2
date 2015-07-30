// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.xray;

import java.util.Map;
import com.newrelic.agent.IRPMService;
import java.util.List;
import com.newrelic.agent.service.Service;

public interface IXRaySessionService extends Service
{
    Map<?, ?> processSessionsList(List<Long> p0, IRPMService p1);
    
    boolean isEnabled();
    
    void addListener(XRaySessionListener p0);
    
    void removeListener(XRaySessionListener p0);
}

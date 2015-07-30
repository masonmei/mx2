// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import java.util.Map;

public interface ConnectionListener
{
    void connected(IRPMService p0, Map<String, Object> p1);
    
    void disconnected(IRPMService p0);
}

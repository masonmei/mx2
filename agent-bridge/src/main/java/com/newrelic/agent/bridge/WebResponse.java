// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

public interface WebResponse
{
    void setStatus(int p0);
    
    int getStatus();
    
    void setStatusMessage(String p0);
    
    String getStatusMessage();
    
    void freezeStatus();
}

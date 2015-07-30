// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.bridge.CrossProcessState;

public interface CrossProcessTransactionState extends CrossProcessState
{
    void writeResponseHeaders();
    
    String getTripId();
    
    int generatePathHash();
    
    String getAlternatePathHashes();
}

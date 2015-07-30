// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import java.util.List;

public interface StatusManager
{
    void add(Status p0);
    
    List<Status> getCopyOfStatusList();
    
    int getCount();
    
    void add(StatusListener p0);
    
    void remove(StatusListener p0);
    
    void clear();
    
    List<StatusListener> getCopyOfStatusListenerList();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.List;

public class StatusListenerAsList implements StatusListener
{
    List<Status> statusList;
    
    public StatusListenerAsList() {
        this.statusList = new ArrayList<Status>();
    }
    
    public void addStatusEvent(final Status status) {
        this.statusList.add(status);
    }
    
    public List<Status> getStatusList() {
        return this.statusList;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional;

import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.InPlayListener;

class ThenActionState implements InPlayListener
{
    List<SaxEvent> eventList;
    boolean isRegistered;
    
    ThenActionState() {
        this.eventList = new ArrayList<SaxEvent>();
        this.isRegistered = false;
    }
    
    public void inPlay(final SaxEvent event) {
        this.eventList.add(event);
    }
}

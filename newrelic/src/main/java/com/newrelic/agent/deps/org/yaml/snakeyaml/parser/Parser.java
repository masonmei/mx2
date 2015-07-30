// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.yaml.snakeyaml.parser;

import com.newrelic.agent.deps.org.yaml.snakeyaml.events.Event;
import java.util.List;

public interface Parser
{
    boolean checkEvent(List<Class<? extends Event>> p0);
    
    boolean checkEvent(Class<? extends Event> p0);
    
    Event peekEvent();
    
    Event getEvent();
}

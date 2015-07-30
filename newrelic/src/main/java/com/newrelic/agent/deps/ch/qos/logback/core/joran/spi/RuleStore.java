// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public interface RuleStore
{
    void addRule(Pattern p0, String p1) throws ClassNotFoundException;
    
    void addRule(Pattern p0, Action p1);
    
    List matchActions(Pattern p0);
}

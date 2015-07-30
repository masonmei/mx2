// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

class IfState
{
    Boolean boolResult;
    List<SaxEvent> thenSaxEventList;
    List<SaxEvent> elseSaxEventList;
    boolean active;
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

public class ThenAction extends ThenOrElseActionBase
{
    void registerEventList(final IfAction ifAction, final List<SaxEvent> eventList) {
        ifAction.setThenSaxEventList(eventList);
    }
}

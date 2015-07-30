// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.EndEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.BodyEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.StartEvent;
import java.util.Collection;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;

public class EventPlayer
{
    final Interpreter interpreter;
    List<SaxEvent> eventList;
    int currentIndex;
    
    public EventPlayer(final Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    
    public List<SaxEvent> getCopyOfPlayerEventList() {
        return new ArrayList<SaxEvent>(this.eventList);
    }
    
    public void play(final List<SaxEvent> aSaxEventList) {
        this.eventList = aSaxEventList;
        this.currentIndex = 0;
        while (this.currentIndex < this.eventList.size()) {
            final SaxEvent se = this.eventList.get(this.currentIndex);
            if (se instanceof StartEvent) {
                this.interpreter.startElement((StartEvent)se);
                this.interpreter.getInterpretationContext().fireInPlay(se);
            }
            if (se instanceof BodyEvent) {
                this.interpreter.getInterpretationContext().fireInPlay(se);
                this.interpreter.characters((BodyEvent)se);
            }
            if (se instanceof EndEvent) {
                this.interpreter.getInterpretationContext().fireInPlay(se);
                this.interpreter.endElement((EndEvent)se);
            }
            ++this.currentIndex;
        }
    }
    
    public void addEventsDynamically(final List<SaxEvent> eventList, final int offset) {
        this.eventList.addAll(this.currentIndex + offset, eventList);
    }
}

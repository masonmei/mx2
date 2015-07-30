// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.InPlayListener;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.Stack;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public abstract class ThenOrElseActionBase extends Action
{
    Stack<ThenActionState> stateStack;
    
    public ThenOrElseActionBase() {
        this.stateStack = new Stack<ThenActionState>();
    }
    
    public void begin(final InterpretationContext ic, final String name, final Attributes attributes) throws ActionException {
        if (!this.weAreActive(ic)) {
            return;
        }
        final ThenActionState state = new ThenActionState();
        if (ic.isListenerListEmpty()) {
            ic.addInPlayListener(state);
            state.isRegistered = true;
        }
        this.stateStack.push(state);
    }
    
    boolean weAreActive(final InterpretationContext ic) {
        final Object o = ic.peekObject();
        if (!(o instanceof IfAction)) {
            return false;
        }
        final IfAction ifAction = (IfAction)o;
        return ifAction.isActive();
    }
    
    public void end(final InterpretationContext ic, final String name) throws ActionException {
        if (!this.weAreActive(ic)) {
            return;
        }
        final ThenActionState state = this.stateStack.pop();
        if (state.isRegistered) {
            ic.removeInPlayListener(state);
            final Object o = ic.peekObject();
            if (!(o instanceof IfAction)) {
                throw new IllegalStateException("Missing IfAction on top of stack");
            }
            final IfAction ifAction = (IfAction)o;
            this.removeFirstAndLastFromList(state.eventList);
            this.registerEventList(ifAction, state.eventList);
        }
    }
    
    abstract void registerEventList(final IfAction p0, final List<SaxEvent> p1);
    
    void removeFirstAndLastFromList(final List<SaxEvent> eventList) {
        eventList.remove(0);
        eventList.remove(eventList.size() - 1);
    }
}

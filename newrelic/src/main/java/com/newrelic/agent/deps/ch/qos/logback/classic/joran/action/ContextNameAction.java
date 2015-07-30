// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran.action;

import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class ContextNameAction extends Action
{
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) {
    }
    
    public void body(final InterpretationContext ec, final String body) {
        final String finalBody = ec.subst(body);
        this.addInfo("Setting logger context name as [" + finalBody + "]");
        try {
            this.context.setName(finalBody);
        }
        catch (IllegalStateException e) {
            this.addError("Failed to rename context [" + this.context.getName() + "] as [" + finalBody + "]", e);
        }
    }
    
    public void end(final InterpretationContext ec, final String name) {
    }
}

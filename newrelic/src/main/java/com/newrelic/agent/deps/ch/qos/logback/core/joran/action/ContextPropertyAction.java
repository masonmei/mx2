// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;

public class ContextPropertyAction extends Action
{
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) throws ActionException {
        this.addError("The [contextProperty] element has been removed. Please use [substitutionProperty] element instead");
    }
    
    public void end(final InterpretationContext ec, final String name) throws ActionException {
    }
}

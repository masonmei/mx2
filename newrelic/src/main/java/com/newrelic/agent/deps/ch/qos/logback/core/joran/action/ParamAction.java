// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.util.PropertySetter;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;

public class ParamAction extends Action
{
    static String NO_NAME;
    static String NO_VALUE;
    boolean inError;
    
    public ParamAction() {
        this.inError = false;
    }
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) {
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        if (name == null) {
            this.inError = true;
            this.addError(ParamAction.NO_NAME);
            return;
        }
        if (value == null) {
            this.inError = true;
            this.addError(ParamAction.NO_VALUE);
            return;
        }
        value = value.trim();
        final Object o = ec.peekObject();
        final PropertySetter propSetter = new PropertySetter(o);
        propSetter.setContext(this.context);
        value = ec.subst(value);
        name = ec.subst(name);
        propSetter.setProperty(name, value);
    }
    
    public void end(final InterpretationContext ec, final String localName) {
    }
    
    public void finish(final InterpretationContext ec) {
    }
    
    static {
        ParamAction.NO_NAME = "No name attribute in <param> element";
        ParamAction.NO_VALUE = "No name attribute in <param> element";
    }
}

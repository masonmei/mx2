// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusListener;

public class StatusListenerAction extends Action
{
    boolean inError;
    StatusListener statusListener;
    
    public StatusListenerAction() {
        this.inError = false;
        this.statusListener = null;
    }
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) throws ActionException {
        this.inError = false;
        final String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            this.addError("Missing class name for statusListener. Near [" + name + "] line " + this.getLineNumber(ec));
            this.inError = true;
            return;
        }
        try {
            this.statusListener = (StatusListener)OptionHelper.instantiateByClassName(className, StatusListener.class, this.context);
            ec.getContext().getStatusManager().add(this.statusListener);
            if (this.statusListener instanceof ContextAware) {
                ((ContextAware)this.statusListener).setContext(this.context);
            }
            this.addInfo("Added status listener of type [" + className + "]");
            ec.pushObject(this.statusListener);
        }
        catch (Exception e) {
            this.inError = true;
            this.addError("Could not create an StatusListener of type [" + className + "].", e);
            throw new ActionException(e);
        }
    }
    
    public void finish(final InterpretationContext ec) {
    }
    
    public void end(final InterpretationContext ec, final String e) {
        if (this.inError) {
            return;
        }
        if (this.statusListener instanceof LifeCycle) {
            ((LifeCycle)this.statusListener).start();
        }
        final Object o = ec.peekObject();
        if (o != this.statusListener) {
            this.addWarn("The object at the of the stack is not the statusListener pushed earlier.");
        }
        else {
            ec.popObject();
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;

public class AppenderAction<E> extends Action
{
    Appender<E> appender;
    private boolean inError;
    
    public AppenderAction() {
        this.inError = false;
    }
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) throws ActionException {
        this.appender = null;
        this.inError = false;
        final String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            this.addError("Missing class name for appender. Near [" + localName + "] line " + this.getLineNumber(ec));
            this.inError = true;
            return;
        }
        try {
            this.addInfo("About to instantiate appender of type [" + className + "]");
            (this.appender = (Appender<E>)OptionHelper.instantiateByClassName(className, Appender.class, this.context)).setContext(this.context);
            final String appenderName = ec.subst(attributes.getValue("name"));
            if (OptionHelper.isEmpty(appenderName)) {
                this.addWarn("No appender name given for appender of type " + className + "].");
            }
            else {
                this.appender.setName(appenderName);
                this.addInfo("Naming appender as [" + appenderName + "]");
            }
            final HashMap<String, Appender> appenderBag = ec.getObjectMap().get("APPENDER_BAG");
            appenderBag.put(appenderName, this.appender);
            ec.pushObject(this.appender);
        }
        catch (Exception oops) {
            this.inError = true;
            this.addError("Could not create an Appender of type [" + className + "].", oops);
            throw new ActionException(oops);
        }
    }
    
    public void end(final InterpretationContext ec, final String name) {
        if (this.inError) {
            return;
        }
        if (this.appender instanceof LifeCycle) {
            this.appender.start();
        }
        final Object o = ec.peekObject();
        if (o != this.appender) {
            this.addWarn("The object at the of the stack is not the appender named [" + this.appender.getName() + "] pushed earlier.");
        }
        else {
            ec.popObject();
        }
    }
}

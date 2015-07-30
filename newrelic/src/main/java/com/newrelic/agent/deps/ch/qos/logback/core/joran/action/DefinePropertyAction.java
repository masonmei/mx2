// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PropertyDefiner;

public class DefinePropertyAction extends Action
{
    String scopeStr;
    ActionUtil.Scope scope;
    String propertyName;
    PropertyDefiner definer;
    boolean inError;
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) throws ActionException {
        this.scopeStr = null;
        this.scope = null;
        this.propertyName = null;
        this.definer = null;
        this.inError = false;
        this.propertyName = attributes.getValue("name");
        this.scopeStr = attributes.getValue("scope");
        this.scope = ActionUtil.stringToScope(this.scopeStr);
        if (OptionHelper.isEmpty(this.propertyName)) {
            this.addError("Missing property name for property definer. Near [" + localName + "] line " + this.getLineNumber(ec));
            this.inError = true;
            return;
        }
        final String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            this.addError("Missing class name for property definer. Near [" + localName + "] line " + this.getLineNumber(ec));
            this.inError = true;
            return;
        }
        try {
            this.addInfo("About to instantiate property definer of type [" + className + "]");
            (this.definer = (PropertyDefiner)OptionHelper.instantiateByClassName(className, PropertyDefiner.class, this.context)).setContext(this.context);
            if (this.definer instanceof LifeCycle) {
                ((LifeCycle)this.definer).start();
            }
            ec.pushObject(this.definer);
        }
        catch (Exception oops) {
            this.inError = true;
            this.addError("Could not create an PropertyDefiner of type [" + className + "].", oops);
            throw new ActionException(oops);
        }
    }
    
    public void end(final InterpretationContext ec, final String name) {
        if (this.inError) {
            return;
        }
        final Object o = ec.peekObject();
        if (o != this.definer) {
            this.addWarn("The object at the of the stack is not the property definer for property named [" + this.propertyName + "] pushed earlier.");
        }
        else {
            this.addInfo("Popping property definer for property named [" + this.propertyName + "] from the object stack");
            ec.popObject();
            final String propertyValue = this.definer.getPropertyValue();
            if (propertyValue != null) {
                ActionUtil.setProperty(ec, this.propertyName, propertyValue, this.scope);
            }
        }
    }
}

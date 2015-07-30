// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.NoAutoStartUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.util.Loader;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.core.util.AggregationType;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.util.PropertySetter;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;
import java.util.Stack;

public class NestedComplexPropertyIA extends ImplicitAction
{
    Stack<IADataForComplexProperty> actionDataStack;
    
    public NestedComplexPropertyIA() {
        this.actionDataStack = new Stack<IADataForComplexProperty>();
    }
    
    public boolean isApplicable(final Pattern pattern, final Attributes attributes, final InterpretationContext ic) {
        final String nestedElementTagName = pattern.peekLast();
        if (ic.isEmpty()) {
            return false;
        }
        final Object o = ic.peekObject();
        final PropertySetter parentBean = new PropertySetter(o);
        parentBean.setContext(this.context);
        final AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);
        switch (aggregationType) {
            case NOT_FOUND:
            case AS_BASIC_PROPERTY:
            case AS_BASIC_PROPERTY_COLLECTION: {
                return false;
            }
            case AS_COMPLEX_PROPERTY_COLLECTION:
            case AS_COMPLEX_PROPERTY: {
                final IADataForComplexProperty ad = new IADataForComplexProperty(parentBean, aggregationType, nestedElementTagName);
                this.actionDataStack.push(ad);
                return true;
            }
            default: {
                this.addError("PropertySetter.computeAggregationType returned " + aggregationType);
                return false;
            }
        }
    }
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) {
        final IADataForComplexProperty actionData = this.actionDataStack.peek();
        String className = attributes.getValue("class");
        className = ec.subst(className);
        Class componentClass = null;
        try {
            if (!OptionHelper.isEmpty(className)) {
                componentClass = Loader.loadClass(className, this.context);
            }
            else {
                final PropertySetter parentBean = actionData.parentBean;
                componentClass = parentBean.getClassNameViaImplicitRules(actionData.getComplexPropertyName(), actionData.getAggregationType(), ec.getDefaultNestedComponentRegistry());
            }
            if (componentClass == null) {
                actionData.inError = true;
                final String errMsg = "Could not find an appropriate class for property [" + localName + "]";
                this.addError(errMsg);
                return;
            }
            if (OptionHelper.isEmpty(className)) {
                this.addInfo("Assuming default type [" + componentClass.getName() + "] for [" + localName + "] property");
            }
            actionData.setNestedComplexProperty(componentClass.newInstance());
            if (actionData.getNestedComplexProperty() instanceof ContextAware) {
                ((ContextAware)actionData.getNestedComplexProperty()).setContext(this.context);
            }
            ec.pushObject(actionData.getNestedComplexProperty());
        }
        catch (Exception oops) {
            actionData.inError = true;
            final String msg = "Could not create component [" + localName + "] of type [" + className + "]";
            this.addError(msg, oops);
        }
    }
    
    public void end(final InterpretationContext ec, final String tagName) {
        final IADataForComplexProperty actionData = this.actionDataStack.pop();
        if (actionData.inError) {
            return;
        }
        final PropertySetter nestedBean = new PropertySetter(actionData.getNestedComplexProperty());
        nestedBean.setContext(this.context);
        if (nestedBean.computeAggregationType("parent") == AggregationType.AS_COMPLEX_PROPERTY) {
            nestedBean.setComplexProperty("parent", actionData.parentBean.getObj());
        }
        final Object nestedComplexProperty = actionData.getNestedComplexProperty();
        if (nestedComplexProperty instanceof LifeCycle && NoAutoStartUtil.notMarkedWithNoAutoStart(nestedComplexProperty)) {
            ((LifeCycle)nestedComplexProperty).start();
        }
        final Object o = ec.peekObject();
        if (o != actionData.getNestedComplexProperty()) {
            this.addError("The object on the top the of the stack is not the component pushed earlier.");
        }
        else {
            ec.popObject();
            switch (actionData.aggregationType) {
                case AS_COMPLEX_PROPERTY: {
                    actionData.parentBean.setComplexProperty(tagName, actionData.getNestedComplexProperty());
                    break;
                }
                case AS_COMPLEX_PROPERTY_COLLECTION: {
                    actionData.parentBean.addComplexProperty(tagName, actionData.getNestedComplexProperty());
                    break;
                }
            }
        }
    }
}

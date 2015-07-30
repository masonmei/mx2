// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.core.util.AggregationType;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.util.PropertySetter;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;
import java.util.Stack;

public class NestedBasicPropertyIA extends ImplicitAction
{
    Stack<IADataForBasicProperty> actionDataStack;
    
    public NestedBasicPropertyIA() {
        this.actionDataStack = new Stack<IADataForBasicProperty>();
    }
    
    public boolean isApplicable(final Pattern pattern, final Attributes attributes, final InterpretationContext ec) {
        final String nestedElementTagName = pattern.peekLast();
        if (ec.isEmpty()) {
            return false;
        }
        final Object o = ec.peekObject();
        final PropertySetter parentBean = new PropertySetter(o);
        parentBean.setContext(this.context);
        final AggregationType aggregationType = parentBean.computeAggregationType(nestedElementTagName);
        switch (aggregationType) {
            case NOT_FOUND:
            case AS_COMPLEX_PROPERTY:
            case AS_COMPLEX_PROPERTY_COLLECTION: {
                return false;
            }
            case AS_BASIC_PROPERTY:
            case AS_BASIC_PROPERTY_COLLECTION: {
                final IADataForBasicProperty ad = new IADataForBasicProperty(parentBean, aggregationType, nestedElementTagName);
                this.actionDataStack.push(ad);
                return true;
            }
            default: {
                this.addError("PropertySetter.canContainComponent returned " + aggregationType);
                return false;
            }
        }
    }
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) {
    }
    
    public void body(final InterpretationContext ec, final String body) {
        final String finalBody = ec.subst(body);
        final IADataForBasicProperty actionData = this.actionDataStack.peek();
        switch (actionData.aggregationType) {
            case AS_BASIC_PROPERTY: {
                actionData.parentBean.setProperty(actionData.propertyName, finalBody);
                break;
            }
            case AS_BASIC_PROPERTY_COLLECTION: {
                actionData.parentBean.addBasicProperty(actionData.propertyName, finalBody);
                break;
            }
        }
    }
    
    public void end(final InterpretationContext ec, final String tagName) {
        this.actionDataStack.pop();
    }
}

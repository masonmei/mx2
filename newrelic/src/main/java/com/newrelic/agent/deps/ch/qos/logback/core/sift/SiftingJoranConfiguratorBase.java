// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.sift;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.ImplicitAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Interpreter;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.GenericConfigurator;

public abstract class SiftingJoranConfiguratorBase<E> extends GenericConfigurator
{
    static final String ONE_AND_ONLY_ONE_URL = "http://logback.qos.ch/codes.html#1andOnly1";
    int errorEmmissionCount;
    
    public SiftingJoranConfiguratorBase() {
        this.errorEmmissionCount = 0;
    }
    
    protected void addImplicitRules(final Interpreter interpreter) {
        final NestedComplexPropertyIA nestedComplexIA = new NestedComplexPropertyIA();
        nestedComplexIA.setContext(this.context);
        interpreter.addImplicitAction(nestedComplexIA);
        final NestedBasicPropertyIA nestedSimpleIA = new NestedBasicPropertyIA();
        nestedSimpleIA.setContext(this.context);
        interpreter.addImplicitAction(nestedSimpleIA);
    }
    
    public abstract Appender<E> getAppender();
    
    protected void oneAndOnlyOneCheck(final Map appenderMap) {
        String errMsg = null;
        if (appenderMap.size() == 0) {
            ++this.errorEmmissionCount;
            errMsg = "No nested appenders found within the <sift> element in SiftingAppender.";
        }
        else if (appenderMap.size() > 1) {
            ++this.errorEmmissionCount;
            errMsg = "Only and only one appender can be nested the <sift> element in SiftingAppender. See also http://logback.qos.ch/codes.html#1andOnly1";
        }
        if (errMsg != null && this.errorEmmissionCount < 4) {
            this.addError(errMsg);
        }
    }
    
    public void doConfigure(final List<SaxEvent> eventList) throws JoranException {
        super.doConfigure(eventList);
    }
}

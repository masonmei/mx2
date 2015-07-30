// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluator;

public abstract class AbstractEventEvaluatorAction extends Action
{
    EventEvaluator evaluator;
    boolean inError;
    
    public AbstractEventEvaluatorAction() {
        this.inError = false;
    }
    
    public void begin(final InterpretationContext ec, final String name, final Attributes attributes) {
        this.inError = false;
        this.evaluator = null;
        String className = attributes.getValue("class");
        if (OptionHelper.isEmpty(className)) {
            className = this.defaultClassName();
            this.addInfo("Assuming default evaluator class [" + className + "]");
        }
        if (OptionHelper.isEmpty(className)) {
            className = this.defaultClassName();
            this.inError = true;
            this.addError("Mandatory \"class\" attribute not set for <evaluator>");
            return;
        }
        final String evaluatorName = attributes.getValue("name");
        if (OptionHelper.isEmpty(evaluatorName)) {
            this.inError = true;
            this.addError("Mandatory \"name\" attribute not set for <evaluator>");
            return;
        }
        try {
            (this.evaluator = (EventEvaluator)OptionHelper.instantiateByClassName(className, EventEvaluator.class, this.context)).setContext(this.context);
            this.evaluator.setName(evaluatorName);
            ec.pushObject(this.evaluator);
            this.addInfo("Adding evaluator named [" + evaluatorName + "] to the object stack");
        }
        catch (Exception oops) {
            this.inError = true;
            this.addError("Could not create evaluator of type " + className + "].", oops);
        }
    }
    
    protected abstract String defaultClassName();
    
    public void end(final InterpretationContext ec, final String e) {
        if (this.inError) {
            return;
        }
        if (this.evaluator instanceof LifeCycle) {
            this.evaluator.start();
            this.addInfo("Starting evaluator named [" + this.evaluator.getName() + "]");
        }
        final Object o = ec.peekObject();
        if (o != this.evaluator) {
            this.addWarn("The object on the top the of the stack is not the evaluator pushed earlier.");
        }
        else {
            ec.popObject();
            try {
                final Map<String, EventEvaluator> evaluatorMap = (Map<String, EventEvaluator>)this.context.getObject("EVALUATOR_MAP");
                if (evaluatorMap == null) {
                    this.addError("Could not find EvaluatorMap");
                }
                else {
                    evaluatorMap.put(this.evaluator.getName(), this.evaluator);
                }
            }
            catch (Exception ex) {
                this.addError("Could not set evaluator named [" + this.evaluator + "].", ex);
            }
        }
    }
    
    public void finish(final InterpretationContext ec) {
    }
}

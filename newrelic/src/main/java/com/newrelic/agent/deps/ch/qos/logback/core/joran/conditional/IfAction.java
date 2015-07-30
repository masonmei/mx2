// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.event.SaxEvent;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Interpreter;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.ActionException;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PropertyContainer;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.core.util.EnvUtil;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.Stack;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;

public class IfAction extends Action
{
    private static final String CONDITION_ATTR = "condition";
    public static final String MISSING_JANINO_MSG = "Could not find Janino library on the class path. Skipping conditional processing.";
    public static final String MISSING_JANINO_SEE = "See also http://logback.qos.ch/codes.html#ifJanino";
    Stack<IfState> stack;
    
    public IfAction() {
        this.stack = new Stack<IfState>();
    }
    
    public void begin(final InterpretationContext ic, final String name, final Attributes attributes) throws ActionException {
        final IfState state = new IfState();
        final boolean emptyStack = this.stack.isEmpty();
        this.stack.push(state);
        if (!emptyStack) {
            return;
        }
        ic.pushObject(this);
        if (!EnvUtil.isJaninoAvailable()) {
            this.addError("Could not find Janino library on the class path. Skipping conditional processing.");
            this.addError("See also http://logback.qos.ch/codes.html#ifJanino");
            return;
        }
        state.active = true;
        Condition condition = null;
        String conditionAttribute = attributes.getValue("condition");
        if (!OptionHelper.isEmpty(conditionAttribute)) {
            conditionAttribute = OptionHelper.substVars(conditionAttribute, ic, this.context);
            final PropertyEvalScriptBuilder pesb = new PropertyEvalScriptBuilder(ic);
            pesb.setContext(this.context);
            try {
                condition = pesb.build(conditionAttribute);
            }
            catch (Exception e) {
                this.addError("Failed to parse condition [" + conditionAttribute + "]", e);
            }
            if (condition != null) {
                state.boolResult = condition.evaluate();
            }
        }
    }
    
    public void end(final InterpretationContext ic, final String name) throws ActionException {
        final IfState state = this.stack.pop();
        if (!state.active) {
            return;
        }
        final Object o = ic.peekObject();
        if (o == null) {
            throw new IllegalStateException("Unexpected null object on stack");
        }
        if (!(o instanceof IfAction)) {
            throw new IllegalStateException("Unexpected object of type [" + o.getClass() + "] on stack");
        }
        if (o != this) {
            throw new IllegalStateException("IfAction different then current one on stack");
        }
        ic.popObject();
        if (state.boolResult == null) {
            this.addError("Failed to determine \"if then else\" result");
            return;
        }
        final Interpreter interpreter = ic.getJoranInterpreter();
        List<SaxEvent> listToPlay = state.thenSaxEventList;
        if (!state.boolResult) {
            listToPlay = state.elseSaxEventList;
        }
        if (listToPlay != null) {
            interpreter.getEventPlayer().addEventsDynamically(listToPlay, 1);
        }
    }
    
    public void setThenSaxEventList(final List<SaxEvent> thenSaxEventList) {
        final IfState state = this.stack.firstElement();
        if (state.active) {
            state.thenSaxEventList = thenSaxEventList;
            return;
        }
        throw new IllegalStateException("setThenSaxEventList() invoked on inactive IfAction");
    }
    
    public void setElseSaxEventList(final List<SaxEvent> elseSaxEventList) {
        final IfState state = this.stack.firstElement();
        if (state.active) {
            state.elseSaxEventList = elseSaxEventList;
            return;
        }
        throw new IllegalStateException("setElseSaxEventList() invoked on inactive IfAction");
    }
    
    public boolean isActive() {
        return this.stack != null && !this.stack.isEmpty() && this.stack.peek().active;
    }
}

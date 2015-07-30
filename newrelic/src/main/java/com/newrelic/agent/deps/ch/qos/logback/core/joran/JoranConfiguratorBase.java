// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran;

import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.Map;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.NestedBasicPropertyIA;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.ImplicitAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.NestedComplexPropertyIA;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Interpreter;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.ParamAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.NewRuleAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.AppenderRefAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.AppenderAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.StatusListenerAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.ConversionRuleAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.ContextPropertyAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.DefinePropertyAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.TimestampAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.PropertyAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.RuleStore;
import java.util.List;

public abstract class JoranConfiguratorBase extends GenericConfigurator
{
    public List getErrorList() {
        return null;
    }
    
    protected void addInstanceRules(final RuleStore rs) {
        rs.addRule(new Pattern("configuration/variable"), new PropertyAction());
        rs.addRule(new Pattern("configuration/property"), new PropertyAction());
        rs.addRule(new Pattern("configuration/substitutionProperty"), new PropertyAction());
        rs.addRule(new Pattern("configuration/timestamp"), new TimestampAction());
        rs.addRule(new Pattern("configuration/define"), new DefinePropertyAction());
        rs.addRule(new Pattern("configuration/contextProperty"), new ContextPropertyAction());
        rs.addRule(new Pattern("configuration/conversionRule"), new ConversionRuleAction());
        rs.addRule(new Pattern("configuration/statusListener"), new StatusListenerAction());
        rs.addRule(new Pattern("configuration/appender"), new AppenderAction<Object>());
        rs.addRule(new Pattern("configuration/appender/appender-ref"), new AppenderRefAction());
        rs.addRule(new Pattern("configuration/newRule"), new NewRuleAction());
        rs.addRule(new Pattern("*/param"), new ParamAction());
    }
    
    protected void addImplicitRules(final Interpreter interpreter) {
        final NestedComplexPropertyIA nestedComplexPropertyIA = new NestedComplexPropertyIA();
        nestedComplexPropertyIA.setContext(this.context);
        interpreter.addImplicitAction(nestedComplexPropertyIA);
        final NestedBasicPropertyIA nestedBasicIA = new NestedBasicPropertyIA();
        nestedBasicIA.setContext(this.context);
        interpreter.addImplicitAction(nestedBasicIA);
    }
    
    protected void buildInterpreter() {
        super.buildInterpreter();
        final Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
        omap.put("APPENDER_BAG", new HashMap());
        omap.put("FILTER_CHAIN_BAG", new HashMap());
    }
    
    public InterpretationContext getExecutionContext() {
        return this.interpreter.getInterpretationContext();
    }
}

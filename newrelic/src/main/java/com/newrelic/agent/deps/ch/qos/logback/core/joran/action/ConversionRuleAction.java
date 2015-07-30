// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.action;

import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.InterpretationContext;

public class ConversionRuleAction extends Action
{
    boolean inError;
    
    public ConversionRuleAction() {
        this.inError = false;
    }
    
    public void begin(final InterpretationContext ec, final String localName, final Attributes attributes) {
        this.inError = false;
        final String conversionWord = attributes.getValue("conversionWord");
        final String converterClass = attributes.getValue("converterClass");
        if (OptionHelper.isEmpty(conversionWord)) {
            this.inError = true;
            final String errorMsg = "No 'conversionWord' attribute in <conversionRule>";
            this.addError(errorMsg);
            return;
        }
        if (OptionHelper.isEmpty(converterClass)) {
            this.inError = true;
            final String errorMsg = "No 'converterClass' attribute in <conversionRule>";
            ec.addError(errorMsg);
            return;
        }
        try {
            Map<String, String> ruleRegistry = (Map<String, String>)this.context.getObject("PATTERN_RULE_REGISTRY");
            if (ruleRegistry == null) {
                ruleRegistry = new HashMap<String, String>();
                this.context.putObject("PATTERN_RULE_REGISTRY", ruleRegistry);
            }
            this.addInfo("registering conversion word " + conversionWord + " with class [" + converterClass + "]");
            ruleRegistry.put(conversionWord, converterClass);
        }
        catch (Exception oops) {
            this.inError = true;
            final String errorMsg = "Could not add conversion rule to PatternLayout.";
            this.addError(errorMsg);
        }
    }
    
    public void end(final InterpretationContext ec, final String n) {
    }
    
    public void finish(final InterpretationContext ec) {
    }
}

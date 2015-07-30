// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.sift;

import java.util.Collection;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import java.util.Map;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.DefaultNestedComponentRules;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.AppenderAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.RuleStore;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;

public class SiftingJoranConfigurator extends SiftingJoranConfiguratorBase<ILoggingEvent>
{
    String key;
    String value;
    
    SiftingJoranConfigurator(final String key, final String value) {
        this.key = key;
        this.value = value;
    }
    
    protected Pattern initialPattern() {
        return new Pattern("configuration");
    }
    
    protected void addInstanceRules(final RuleStore rs) {
        rs.addRule(new Pattern("configuration/appender"), new AppenderAction<Object>());
    }
    
    protected void addDefaultNestedComponentRegistryRules(final DefaultNestedComponentRegistry registry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }
    
    protected void buildInterpreter() {
        super.buildInterpreter();
        final Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
        omap.put("APPENDER_BAG", new HashMap());
        omap.put("FILTER_CHAIN_BAG", new HashMap());
        final Map<String, String> propertiesMap = new HashMap<String, String>();
        propertiesMap.put(this.key, this.value);
        this.interpreter.setInterpretationContextPropertiesMap(propertiesMap);
    }
    
    public Appender<ILoggingEvent> getAppender() {
        final Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
        final HashMap appenderMap = omap.get("APPENDER_BAG");
        this.oneAndOnlyOneCheck(appenderMap);
        final Collection values = appenderMap.values();
        if (values.size() == 0) {
            return null;
        }
        return values.iterator().next();
    }
}

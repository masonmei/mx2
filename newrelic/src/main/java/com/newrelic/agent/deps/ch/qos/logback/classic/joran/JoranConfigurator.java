// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran;

import com.newrelic.agent.deps.ch.qos.logback.classic.util.DefaultNestedComponentRules;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.ConsolePluginAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.IncludeAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.JMXConfiguratorAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.PlatformInfo;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional.ElseAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional.ThenAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional.IfAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.AppenderRefAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.RootLoggerAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.LevelAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.LoggerAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.NOPAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.sift.SiftAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.EvaluatorAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.InsertFromJNDIAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.LoggerContextListenerAction;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.ContextNameAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.Action;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.action.ConfigurationAction;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.Pattern;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.RuleStore;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.JoranConfiguratorBase;

public class JoranConfigurator extends JoranConfiguratorBase
{
    public void addInstanceRules(final RuleStore rs) {
        super.addInstanceRules(rs);
        rs.addRule(new Pattern("configuration"), new ConfigurationAction());
        rs.addRule(new Pattern("configuration/contextName"), new ContextNameAction());
        rs.addRule(new Pattern("configuration/contextListener"), new LoggerContextListenerAction());
        rs.addRule(new Pattern("configuration/insertFromJNDI"), new InsertFromJNDIAction());
        rs.addRule(new Pattern("configuration/evaluator"), new EvaluatorAction());
        rs.addRule(new Pattern("configuration/appender/sift"), new SiftAction());
        rs.addRule(new Pattern("configuration/appender/sift/*"), new NOPAction());
        rs.addRule(new Pattern("configuration/logger"), new LoggerAction());
        rs.addRule(new Pattern("configuration/logger/level"), new LevelAction());
        rs.addRule(new Pattern("configuration/root"), new RootLoggerAction());
        rs.addRule(new Pattern("configuration/root/level"), new LevelAction());
        rs.addRule(new Pattern("configuration/logger/appender-ref"), new AppenderRefAction());
        rs.addRule(new Pattern("configuration/root/appender-ref"), new AppenderRefAction());
        rs.addRule(new Pattern("*/if"), new IfAction());
        rs.addRule(new Pattern("*/if/then"), new ThenAction());
        rs.addRule(new Pattern("*/if/then/*"), new NOPAction());
        rs.addRule(new Pattern("*/if/else"), new ElseAction());
        rs.addRule(new Pattern("*/if/else/*"), new NOPAction());
        if (PlatformInfo.hasJMXObjectName()) {
            rs.addRule(new Pattern("configuration/jmxConfigurator"), new JMXConfiguratorAction());
        }
        rs.addRule(new Pattern("configuration/include"), new IncludeAction());
        rs.addRule(new Pattern("configuration/consolePlugin"), new ConsolePluginAction());
    }
    
    protected void addDefaultNestedComponentRegistryRules(final DefaultNestedComponentRegistry registry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }
}

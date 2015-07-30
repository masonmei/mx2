// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.config.BaseConfig;
import java.util.Map;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Collections;
import com.newrelic.agent.config.Config;

public class PointCutConfiguration
{
    private final String name;
    private final String groupName;
    private final Config config;
    private final boolean enabledByDefault;
    
    public PointCutConfiguration(final Class<? extends PointCut> pc) {
        this(pc.getName(), null, true);
    }
    
    public PointCutConfiguration(final String configurationName) {
        this(configurationName, null, true);
    }
    
    public PointCutConfiguration(final String configurationName, final boolean enabledByDefault) {
        this(configurationName, null, enabledByDefault);
    }
    
    public PointCutConfiguration(final String configurationName, final String configurationGroupName, final boolean enabledByDefault) {
        this.name = configurationName;
        this.groupName = configurationGroupName;
        this.enabledByDefault = enabledByDefault;
        this.config = this.initConfig(configurationName);
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final String getGroupName() {
        return this.groupName;
    }
    
    public Config getConfiguration() {
        return this.config;
    }
    
    private Config initConfig(final String name) {
        Map<String, Object> config = Collections.emptyMap();
        if (name != null) {
            final Object pointCutConfig = ServiceFactory.getConfigService().getDefaultAgentConfig().getClassTransformerConfig().getProperty(name);
            if (pointCutConfig instanceof Map) {
                config = (Map<String, Object>)pointCutConfig;
            }
        }
        return new BaseConfig(config);
    }
    
    protected final Config getGroupConfig() {
        return this.initConfig(this.groupName);
    }
    
    public boolean isEnabled() {
        if (!this.getGroupConfig().getProperty("enabled", true)) {
            final String msg = MessageFormat.format("Disabled point cut \"{0}\" (\"{1}\" group)", this.getName(), this.getGroupName());
            Agent.LOG.info(msg);
            return false;
        }
        final Config pointCutConfig = this.getConfiguration();
        final boolean val = pointCutConfig.getProperty("enabled", this.isEnabledByDefault());
        if (val != this.isEnabledByDefault()) {
            final String msg2 = MessageFormat.format("{0}abled point cut \"{1}\"", val ? "En" : "Dis", this.getName());
            Agent.LOG.info(msg2);
        }
        return val;
    }
    
    protected boolean isEnabledByDefault() {
        return this.enabledByDefault;
    }
}

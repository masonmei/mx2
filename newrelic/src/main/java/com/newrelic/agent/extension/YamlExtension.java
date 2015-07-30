// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import com.newrelic.agent.config.PointCutConfig;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.Iterator;
import java.util.Collections;
import com.newrelic.agent.jmx.create.JmxYmlParser;
import com.newrelic.agent.deps.com.google.common.collect.Lists;
import java.util.List;
import com.newrelic.agent.jmx.create.JmxConfiguration;
import java.util.Collection;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.config.BaseConfig;
import java.util.Map;
import com.newrelic.agent.config.Config;

public class YamlExtension extends Extension
{
    private final Config configuration;
    private final boolean enabled;
    
    public YamlExtension(final ClassLoader classloader, final String name, final Map<String, Object> configuration, final boolean custom) throws IllegalArgumentException {
        super(classloader, name, custom);
        if (name == null) {
            throw new IllegalArgumentException("Extensions must have a name");
        }
        this.configuration = new BaseConfig(configuration);
        this.enabled = this.configuration.getProperty("enabled", true);
    }
    
    YamlExtension(final ClassLoader classloader, final Map<String, Object> config, final boolean custom) {
        this(classloader, config.get("name"), config, custom);
    }
    
    public String toString() {
        return this.getName() + " Extension";
    }
    
    public final Config getConfiguration() {
        return this.configuration;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public String getVersion() {
        return this.configuration.getProperty("version", "n/a");
    }
    
    public double getVersionNumber() {
        try {
            return this.configuration.getProperty("version", 0.0);
        }
        catch (Exception e) {
            Agent.LOG.severe(MessageFormat.format("Extension \"{0}\" has an invalid version number: {1}: {2}", this.getName(), e.getClass().getSimpleName(), e.getMessage()));
            return 0.0;
        }
    }
    
    public Collection<JmxConfiguration> getJmxConfig() {
        final Object jmx = this.getConfiguration().getProperty("jmx");
        if (jmx != null && jmx instanceof List) {
            final List<JmxConfiguration> list = (List<JmxConfiguration>)Lists.newArrayList();
            for (final Map config : (List)jmx) {
                list.add(new JmxYmlParser(config));
            }
            return list;
        }
        return (Collection<JmxConfiguration>)Collections.emptyList();
    }
    
    public Collection<ExtensionClassAndMethodMatcher> getInstrumentationMatchers() {
        if (this.isEnabled()) {
            final Object instrumentation = this.getConfiguration().getProperty("instrumentation");
            if (instrumentation instanceof Map) {
                return PointCutConfig.getExtensionPointCuts(this, (Map)instrumentation);
            }
            if (this.configuration.getProperty("jmx", (Object)null) == null) {
                final String msg = MessageFormat.format("Extension {0} either does not have an instrumentation section or has an invalid instrumentation section. Please check the format of the file.", this.getName());
                Agent.LOG.severe(msg);
            }
        }
        return (Collection<ExtensionClassAndMethodMatcher>)Collections.emptyList();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Collections;
import java.util.Map;
import java.util.Collection;

class JmxConfigImpl extends BaseConfig implements JmxConfig
{
    public static final String ENABLED = "enabled";
    public static final String CREATE_MBEAN_SERVER = "create_mbean_server";
    public static final String DISABLED_JMX_FRAMEWORKS = "disabled_jmx_frameworks";
    public static final Boolean DEFAULT_ENABLED;
    public static final Boolean DEFAULT_CREATE_MBEAN_SERVER;
    public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.jmx.";
    private final boolean isEnabled;
    private final boolean isCreateMbeanServer;
    private final Collection<String> disabledJmxFrameworks;
    
    public JmxConfigImpl(final Map<String, Object> pProps) {
        super(pProps, "newrelic.config.jmx.");
        this.isEnabled = this.getProperty("enabled", JmxConfigImpl.DEFAULT_ENABLED);
        this.isCreateMbeanServer = this.getProperty("create_mbean_server", JmxConfigImpl.DEFAULT_CREATE_MBEAN_SERVER);
        this.disabledJmxFrameworks = this.getUniqueStrings("disabled_jmx_frameworks", ",");
    }
    
    static JmxConfigImpl createJmxConfig(Map<String, Object> settings) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new JmxConfigImpl(settings);
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public boolean isCreateMbeanServer() {
        return this.isCreateMbeanServer;
    }
    
    public Collection<String> getDisabledJmxFrameworks() {
        return this.disabledJmxFrameworks;
    }
    
    static {
        DEFAULT_ENABLED = Boolean.TRUE;
        DEFAULT_CREATE_MBEAN_SERVER = Boolean.TRUE;
    }
}

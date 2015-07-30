// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Collections;
import java.util.Map;

public class JarCollectorConfigImpl extends BaseConfig implements JarCollectorConfig
{
    public static final Integer DEFAULT_MAX_CLASS_LOADERS;
    public static final String ENABLED = "enabled";
    public static final String MAX_CLASS_LOADERS = "max_class_loaders";
    public static final Boolean DEFAULT_ENABLED;
    public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.module.";
    private final boolean isEnabled;
    private final int maxClassLoaders;
    
    public JarCollectorConfigImpl(final Map<String, Object> pProps) {
        super(pProps, "newrelic.config.module.");
        this.isEnabled = this.getProperty("enabled", JarCollectorConfigImpl.DEFAULT_ENABLED);
        this.maxClassLoaders = this.getProperty("max_class_loaders", JarCollectorConfigImpl.DEFAULT_MAX_CLASS_LOADERS);
    }
    
    static JarCollectorConfigImpl createJarCollectorConfig(Map<String, Object> settings) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new JarCollectorConfigImpl(settings);
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public int getMaxClassLoaders() {
        return this.maxClassLoaders;
    }
    
    static {
        DEFAULT_MAX_CLASS_LOADERS = 5000;
        DEFAULT_ENABLED = Boolean.TRUE;
    }
}

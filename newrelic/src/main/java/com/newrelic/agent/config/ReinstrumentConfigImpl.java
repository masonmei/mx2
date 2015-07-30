// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Collections;
import java.util.Map;

class ReinstrumentConfigImpl extends BaseConfig implements ReinstrumentConfig
{
    public static final String ENABLED = "enabled";
    public static final Boolean DEFAULT_ENABLED;
    public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.reinstrument.";
    public static final String ATTS_ENABLED = "attributes_enabled";
    public static final Boolean DEFAULT_ATTS_ENABLED;
    private final boolean isEnabled;
    private final boolean isAttsEnabled;
    
    public ReinstrumentConfigImpl(final Map<String, Object> pProps) {
        super(pProps, "newrelic.config.reinstrument.");
        this.isEnabled = this.getProperty("enabled", ReinstrumentConfigImpl.DEFAULT_ENABLED);
        this.isAttsEnabled = this.getProperty("attributes_enabled", ReinstrumentConfigImpl.DEFAULT_ATTS_ENABLED);
    }
    
    static ReinstrumentConfigImpl createReinstrumentConfig(Map<String, Object> settings) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new ReinstrumentConfigImpl(settings);
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public boolean isAttributesEnabled() {
        return this.isAttsEnabled;
    }
    
    static {
        DEFAULT_ENABLED = Boolean.TRUE;
        DEFAULT_ATTS_ENABLED = Boolean.FALSE;
    }
}

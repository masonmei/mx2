// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StripExceptionConfigImpl extends BaseConfig implements StripExceptionConfig
{
    public static final String ENABLED = "enabled";
    public static final String WHITELIST = "whitelist";
    public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.strip_exception_messages.";
    private final boolean isEnabled;
    private final Set<String> whitelist;
    
    private StripExceptionConfigImpl(final Map<String, Object> props, final boolean highSecurity) {
        super(props, "newrelic.config.strip_exception_messages.");
        this.isEnabled = this.getProperty("enabled", highSecurity);
        this.whitelist = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(this.getUniqueStrings("whitelist")));
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public Set<String> getWhitelist() {
        return this.whitelist;
    }
    
    static StripExceptionConfig createStripExceptionConfig(Map<String, Object> settings, final boolean highSecurity) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new StripExceptionConfigImpl(settings, highSecurity);
    }
}

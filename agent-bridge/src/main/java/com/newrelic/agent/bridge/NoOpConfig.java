// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.Config;

public class NoOpConfig implements Config
{
    public static final Config Instance;
    
    public <T> T getValue(final String prop) {
        return null;
    }
    
    public <T> T getValue(final String key, final T defaultVal) {
        return defaultVal;
    }
    
    static {
        Instance = (Config)new NoOpConfig();
    }
}

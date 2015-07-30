// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import com.newrelic.agent.deps.org.yaml.snakeyaml.constructor.Construct;

public abstract class ConfigurationConstruct implements Construct
{
    private final String name;
    
    public ConfigurationConstruct(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

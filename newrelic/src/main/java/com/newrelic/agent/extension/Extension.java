// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import com.newrelic.agent.jmx.create.JmxConfiguration;
import java.util.Collection;

public abstract class Extension
{
    private final String name;
    private final ClassLoader classloader;
    private final boolean custom;
    
    public Extension(final ClassLoader classloader, final String name, final boolean custom) {
        if (name == null) {
            throw new IllegalArgumentException("Extensions must have a name");
        }
        this.classloader = classloader;
        this.name = name;
        this.custom = custom;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final ClassLoader getClassLoader() {
        return this.classloader;
    }
    
    public String toString() {
        return this.getName() + " Extension";
    }
    
    public boolean isCustom() {
        return this.custom;
    }
    
    public abstract boolean isEnabled();
    
    public abstract String getVersion();
    
    public abstract double getVersionNumber();
    
    public abstract Collection<JmxConfiguration> getJmxConfig();
    
    public abstract Collection<ExtensionClassAndMethodMatcher> getInstrumentationMatchers();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension;

import com.newrelic.agent.extension.util.ExtensionConversionUtility;
import com.newrelic.agent.instrumentation.InstrumentationType;
import java.util.Arrays;
import com.newrelic.agent.instrumentation.custom.ExtensionClassAndMethodMatcher;
import java.util.Collections;
import com.newrelic.agent.jmx.create.JmxConfiguration;
import java.util.Collection;

class XmlExtension extends Extension
{
    private final com.newrelic.agent.extension.beans.Extension extension;
    
    public XmlExtension(final ClassLoader classloader, final String name, final com.newrelic.agent.extension.beans.Extension ext, final boolean custom) {
        super(classloader, name, custom);
        this.extension = ext;
    }
    
    public boolean isEnabled() {
        return this.extension.isEnabled();
    }
    
    public String getVersion() {
        return Double.toString(this.extension.getVersion());
    }
    
    public double getVersionNumber() {
        return this.extension.getVersion();
    }
    
    public Collection<JmxConfiguration> getJmxConfig() {
        return (Collection<JmxConfiguration>)Collections.emptyList();
    }
    
    public Collection<ExtensionClassAndMethodMatcher> getInstrumentationMatchers() {
        if (this.isEnabled()) {
            return ExtensionConversionUtility.convertToEnabledPointCuts(Arrays.asList(this.extension), this.isCustom(), InstrumentationType.LocalCustomXml);
        }
        return (Collection<ExtensionClassAndMethodMatcher>)Collections.emptyList();
    }
}

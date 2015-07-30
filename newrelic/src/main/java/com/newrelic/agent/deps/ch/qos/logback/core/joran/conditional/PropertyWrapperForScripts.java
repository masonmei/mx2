// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.joran.conditional;

import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PropertyContainer;

public class PropertyWrapperForScripts
{
    PropertyContainer local;
    PropertyContainer context;
    
    public void setPropertyContainers(final PropertyContainer local, final PropertyContainer context) {
        this.local = local;
        this.context = context;
    }
    
    public boolean isNull(final String k) {
        final String val = OptionHelper.propertyLookup(k, this.local, this.context);
        return val == null;
    }
    
    public boolean isDefined(final String k) {
        final String val = OptionHelper.propertyLookup(k, this.local, this.context);
        return val != null;
    }
    
    public String p(final String k) {
        return this.property(k);
    }
    
    public String property(final String k) {
        final String val = OptionHelper.propertyLookup(k, this.local, this.context);
        if (val != null) {
            return val;
        }
        return "";
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Collection;

public interface JmxConfig
{
    boolean isEnabled();
    
    boolean isCreateMbeanServer();
    
    Collection<String> getDisabledJmxFrameworks();
}

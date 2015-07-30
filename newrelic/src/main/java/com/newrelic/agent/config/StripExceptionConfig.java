// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Set;

public interface StripExceptionConfig
{
    boolean isEnabled();
    
    Set<String> getWhitelist();
}

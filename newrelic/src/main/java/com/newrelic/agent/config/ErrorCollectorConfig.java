// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Set;

public interface ErrorCollectorConfig
{
    boolean isEnabled();
    
    Set<Integer> getIgnoreStatusCodes();
    
    Set<String> getIgnoreErrors();
    
     <T> T getProperty(String p0);
    
     <T> T getProperty(String p0, T p1);
}

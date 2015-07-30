// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Set;

public interface BrowserMonitoringConfig
{
    boolean isAutoInstrumentEnabled();
    
    Set<String> getDisabledAutoPages();
    
    String getLoaderType();
    
    boolean isDebug();
    
    boolean isSslForHttp();
    
    boolean isSslForHttpSet();
    
    boolean isAllowMultipleFooters();
}

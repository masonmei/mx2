// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.create;

import java.util.List;
import com.newrelic.agent.jmx.JmxType;
import java.util.Map;

public interface JmxConfiguration
{
    String getObjectName();
    
    String getRootMetricName();
    
    boolean getEnabled();
    
    Map<JmxType, List<String>> getAttrs();
}

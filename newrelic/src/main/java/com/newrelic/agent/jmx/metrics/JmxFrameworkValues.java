// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx.metrics;

import java.util.List;

public abstract class JmxFrameworkValues
{
    public abstract List<BaseJmxValue> getFrameworkMetrics();
    
    public List<BaseJmxInvokeValue> getJmxInvokers() {
        return null;
    }
    
    public abstract String getPrefix();
}

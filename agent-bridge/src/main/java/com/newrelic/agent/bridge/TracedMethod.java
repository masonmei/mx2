// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import com.newrelic.api.agent.TracedMethod;

public interface TracedMethod extends com.newrelic.api.agent.TracedMethod
{
    TracedMethod getParentTracedMethod();
    
    void setRollupMetricNames(String... p0);
    
    void setMetricNameFormatInfo(String p0, String p1, String p2);
    
    void addExclusiveRollupMetricName(String... p0);
    
    void nameTransaction(TransactionNamePriority p0);
}

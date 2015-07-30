// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.sql;

import java.util.Map;

public interface SqlTrace
{
    int getId();
    
    String getSql();
    
    int getCallCount();
    
    long getTotal();
    
    long getMax();
    
    long getMin();
    
    String getBlameMetricName();
    
    String getUri();
    
    String getMetricName();
    
    Map<String, Object> getParameters();
}

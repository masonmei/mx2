// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public interface HttpConnectionMetrics
{
    long getRequestCount();
    
    long getResponseCount();
    
    long getSentBytesCount();
    
    long getReceivedBytesCount();
    
    Object getMetric(String p0);
    
    void reset();
}

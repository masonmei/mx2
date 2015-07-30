// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.io;

public interface HttpTransportMetrics
{
    long getBytesTransferred();
    
    void reset();
}

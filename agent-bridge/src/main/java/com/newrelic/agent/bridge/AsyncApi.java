// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

public interface AsyncApi
{
    @Deprecated
    void errorAsync(Object p0, Throwable p1);
    
    @Deprecated
    void suspendAsync(Object p0);
    
    @Deprecated
    Transaction resumeAsync(Object p0);
    
    @Deprecated
    void completeAsync(Object p0);
    
    @Deprecated
    void finishRootTracer();
}

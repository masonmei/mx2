// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.concurrent;

public interface FutureCallback<T>
{
    void completed(T p0);
    
    void failed(Exception p0);
    
    void cancelled();
}

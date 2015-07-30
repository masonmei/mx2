// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

public class NoOpAsyncApi implements AsyncApi
{
    public void errorAsync(final Object context, final Throwable t) {
    }
    
    public void suspendAsync(final Object asyncContext) {
    }
    
    public Transaction resumeAsync(final Object asyncContext) {
        return null;
    }
    
    public void completeAsync(final Object asyncContext) {
    }
    
    public void finishRootTracer() {
    }
}

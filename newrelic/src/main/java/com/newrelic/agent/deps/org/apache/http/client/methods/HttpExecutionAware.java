// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client.methods;

import com.newrelic.agent.deps.org.apache.http.concurrent.Cancellable;

public interface HttpExecutionAware
{
    boolean isAborted();
    
    void setCancellable(Cancellable p0);
}

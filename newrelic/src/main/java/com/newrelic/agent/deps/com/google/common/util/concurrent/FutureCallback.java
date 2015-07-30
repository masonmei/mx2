// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import javax.annotation.Nullable;

public interface FutureCallback<V>
{
    void onSuccess(@Nullable V p0);
    
    void onFailure(Throwable p0);
}

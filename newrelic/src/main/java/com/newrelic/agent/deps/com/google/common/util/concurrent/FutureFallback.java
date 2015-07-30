// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface FutureFallback<V>
{
    ListenableFuture<V> create(Throwable p0) throws Exception;
}

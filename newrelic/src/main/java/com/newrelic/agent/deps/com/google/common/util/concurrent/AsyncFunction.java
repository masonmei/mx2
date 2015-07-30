// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

public interface AsyncFunction<I, O>
{
    ListenableFuture<O> apply(I p0) throws Exception;
}

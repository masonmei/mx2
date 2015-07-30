// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface CheckedFuture<V, X extends Exception> extends ListenableFuture<V>
{
    V checkedGet() throws X, Exception;
    
    V checkedGet(long p0, TimeUnit p1) throws TimeoutException, X, Exception;
}

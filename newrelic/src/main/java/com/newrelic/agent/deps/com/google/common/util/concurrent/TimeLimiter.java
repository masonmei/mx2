// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface TimeLimiter
{
     <T> T newProxy(T p0, Class<T> p1, long p2, TimeUnit p3);
    
     <T> T callWithTimeout(Callable<T> p0, long p1, TimeUnit p2, boolean p3) throws Exception;
}

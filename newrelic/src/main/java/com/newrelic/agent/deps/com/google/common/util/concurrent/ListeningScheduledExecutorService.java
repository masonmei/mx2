// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import java.util.concurrent.ScheduledExecutorService;

@Beta
public interface ListeningScheduledExecutorService extends ScheduledExecutorService, ListeningExecutorService
{
    ListenableScheduledFuture<?> schedule(Runnable p0, long p1, TimeUnit p2);
    
     <V> ListenableScheduledFuture<V> schedule(Callable<V> p0, long p1, TimeUnit p2);
    
    ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable p0, long p1, long p2, TimeUnit p3);
    
    ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable p0, long p1, long p2, TimeUnit p3);
}

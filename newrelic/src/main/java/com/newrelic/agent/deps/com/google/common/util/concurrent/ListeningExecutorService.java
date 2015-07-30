// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.List;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public interface ListeningExecutorService extends ExecutorService
{
     <T> ListenableFuture<T> submit(Callable<T> p0);
    
    ListenableFuture<?> submit(Runnable p0);
    
     <T> ListenableFuture<T> submit(Runnable p0, T p1);
    
     <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> p0) throws InterruptedException;
    
     <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> p0, long p1, TimeUnit p2) throws InterruptedException;
}

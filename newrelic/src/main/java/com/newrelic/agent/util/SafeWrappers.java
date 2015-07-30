// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import com.newrelic.agent.Agent;

public class SafeWrappers
{
    public static Runnable safeRunnable(final Runnable runnable) {
        return new Runnable() {
            public void run() {
                try {
                    runnable.run();
                }
                catch (Throwable t) {
                    try {
                        Agent.LOG.log(Level.SEVERE, t.toString());
                        Agent.LOG.log(Level.FINEST, t, t.toString(), new Object[0]);
                    }
                    catch (Throwable t2) {}
                }
            }
        };
    }
    
    public static ScheduledExecutorService safeExecutor(final ScheduledExecutorService executor) {
        return new ScheduledExecutorService() {
            public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
                return executor.schedule(SafeWrappers.safeRunnable(command), delay, unit);
            }
            
            public void execute(final Runnable command) {
                executor.execute(command);
            }
            
            public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
                throw new UnsupportedOperationException();
            }
            
            public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
                return executor.scheduleAtFixedRate(SafeWrappers.safeRunnable(command), initialDelay, period, unit);
            }
            
            public void shutdown() {
                throw new UnsupportedOperationException();
            }
            
            public List<Runnable> shutdownNow() {
                throw new UnsupportedOperationException();
            }
            
            public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay, final TimeUnit unit) {
                return executor.scheduleWithFixedDelay(SafeWrappers.safeRunnable(command), initialDelay, delay, unit);
            }
            
            public boolean isShutdown() {
                return executor.isShutdown();
            }
            
            public boolean isTerminated() {
                return executor.isTerminated();
            }
            
            public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
                throw new UnsupportedOperationException();
            }
            
            public <T> Future<T> submit(final Callable<T> task) {
                throw new UnsupportedOperationException();
            }
            
            public <T> Future<T> submit(final Runnable task, final T result) {
                return executor.submit(SafeWrappers.safeRunnable(task), result);
            }
            
            public Future<?> submit(final Runnable task) {
                return executor.submit(SafeWrappers.safeRunnable(task));
            }
            
            public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
                throw new UnsupportedOperationException();
            }
            
            public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
                throw new UnsupportedOperationException();
            }
            
            public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
                throw new UnsupportedOperationException();
            }
            
            public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                throw new UnsupportedOperationException();
            }
        };
    }
}

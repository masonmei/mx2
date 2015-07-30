// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.concurrent.CountDownLatch;

public class LatchingRunnable implements Runnable
{
    final CountDownLatch latch;
    
    public LatchingRunnable() {
        this.latch = new CountDownLatch(1);
    }
    
    public void run() {
        this.latch.countDown();
    }
    
    public void block() {
        try {
            this.latch.await();
        }
        catch (InterruptedException e) {
            Agent.LOG.log(Level.FINE, "Latch error", e);
        }
    }
    
    public static void drain(final Executor executor) {
        final LatchingRunnable runnable = new LatchingRunnable();
        try {
            executor.execute(runnable);
            runnable.block();
        }
        catch (RejectedExecutionException e) {
            Agent.LOG.finest("Unable to drain executor");
        }
    }
}

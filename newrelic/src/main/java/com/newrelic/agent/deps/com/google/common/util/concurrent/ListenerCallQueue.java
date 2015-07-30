// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import java.util.Iterator;
import java.util.logging.Level;
import com.newrelic.agent.deps.com.google.common.base.Preconditions;
import com.newrelic.agent.deps.com.google.common.collect.Queues;
import javax.annotation.concurrent.GuardedBy;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

final class ListenerCallQueue<L> implements Runnable
{
    private static final Logger logger;
    private final L listener;
    private final Executor executor;
    @GuardedBy("this")
    private final Queue<Callback<L>> waitQueue;
    @GuardedBy("this")
    private boolean isThreadScheduled;
    
    ListenerCallQueue(final L listener, final Executor executor) {
        this.waitQueue = (Queue<Callback<L>>)Queues.newArrayDeque();
        this.listener = Preconditions.checkNotNull(listener);
        this.executor = Preconditions.checkNotNull(executor);
    }
    
    synchronized void add(final Callback<L> callback) {
        this.waitQueue.add(callback);
    }
    
    void execute() {
        boolean scheduleTaskRunner = false;
        synchronized (this) {
            if (!this.isThreadScheduled) {
                this.isThreadScheduled = true;
                scheduleTaskRunner = true;
            }
        }
        if (scheduleTaskRunner) {
            try {
                this.executor.execute(this);
            }
            catch (RuntimeException e) {
                synchronized (this) {
                    this.isThreadScheduled = false;
                }
                final Logger logger = ListenerCallQueue.logger;
                final Level severe = Level.SEVERE;
                final String value = String.valueOf(String.valueOf(this.listener));
                final String value2 = String.valueOf(String.valueOf(this.executor));
                logger.log(severe, new StringBuilder(42 + value.length() + value2.length()).append("Exception while running callbacks for ").append(value).append(" on ").append(value2).toString(), e);
                throw e;
            }
        }
    }
    
    @Override
    public void run() {
        boolean stillRunning = true;
        try {
            while (true) {
                final Callback<L> nextToRun;
                synchronized (this) {
                    Preconditions.checkState(this.isThreadScheduled);
                    nextToRun = this.waitQueue.poll();
                    if (nextToRun == null) {
                        this.isThreadScheduled = false;
                        stillRunning = false;
                        break;
                    }
                }
                try {
                    nextToRun.call(this.listener);
                }
                catch (RuntimeException e) {
                    final Logger logger = ListenerCallQueue.logger;
                    final Level severe = Level.SEVERE;
                    final String value = String.valueOf(String.valueOf(this.listener));
                    final String value2 = String.valueOf(String.valueOf(((Callback<Object>)nextToRun).methodCall));
                    logger.log(severe, new StringBuilder(37 + value.length() + value2.length()).append("Exception while executing callback: ").append(value).append(".").append(value2).toString(), e);
                }
            }
        }
        finally {
            if (stillRunning) {
                synchronized (this) {
                    this.isThreadScheduled = false;
                }
            }
        }
    }
    
    static {
        ListenerCallQueue.class.getName();
        logger = Logger.global;
    }
    
    abstract static class Callback<L>
    {
        private final String methodCall;
        
        Callback(final String methodCall) {
            this.methodCall = methodCall;
        }
        
        abstract void call(final L p0);
        
        void enqueueOn(final Iterable<ListenerCallQueue<L>> queues) {
            for (final ListenerCallQueue<L> queue : queues) {
                queue.add(this);
            }
        }
    }
}

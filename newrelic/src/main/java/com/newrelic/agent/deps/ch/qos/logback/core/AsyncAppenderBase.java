// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.AppenderAttachable;

public class AsyncAppenderBase<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E>
{
    AppenderAttachableImpl<E> aai;
    BlockingQueue<E> blockingQueue;
    public static final int DEFAULT_QUEUE_SIZE = 256;
    int queueSize;
    int appenderCount;
    static final int UNDEFINED = -1;
    int discardingThreshold;
    Worker worker;
    
    public AsyncAppenderBase() {
        this.aai = new AppenderAttachableImpl<E>();
        this.queueSize = 256;
        this.appenderCount = 0;
        this.discardingThreshold = -1;
        this.worker = new Worker();
    }
    
    protected boolean isDiscardable(final E eventObject) {
        return false;
    }
    
    protected void preprocess(final E eventObject) {
    }
    
    public void start() {
        if (this.appenderCount == 0) {
            this.addError("No attached appenders found.");
            return;
        }
        if (this.queueSize < 1) {
            this.addError("Invalid queue size [" + this.queueSize + "]");
            return;
        }
        this.blockingQueue = new ArrayBlockingQueue<E>(this.queueSize);
        if (this.discardingThreshold == -1) {
            this.discardingThreshold = this.queueSize / 5;
        }
        this.addInfo("Setting discardingThreshold to " + this.discardingThreshold);
        this.worker.setDaemon(true);
        this.worker.setName("AsyncAppender-Worker-" + this.worker.getName());
        super.start();
        this.worker.start();
    }
    
    public void stop() {
        if (!this.isStarted()) {
            return;
        }
        super.stop();
        this.worker.interrupt();
        try {
            this.worker.join(1000L);
        }
        catch (InterruptedException e) {
            this.addError("Failed to join worker thread", e);
        }
    }
    
    protected void append(final E eventObject) {
        if (this.isQueueBelowDiscardingThreshold() && this.isDiscardable(eventObject)) {
            return;
        }
        this.preprocess(eventObject);
        this.put(eventObject);
    }
    
    private boolean isQueueBelowDiscardingThreshold() {
        return this.blockingQueue.remainingCapacity() < this.discardingThreshold;
    }
    
    private void put(final E eventObject) {
        try {
            this.blockingQueue.put(eventObject);
        }
        catch (InterruptedException ex) {}
    }
    
    public int getQueueSize() {
        return this.queueSize;
    }
    
    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }
    
    public int getDiscardingThreshold() {
        return this.discardingThreshold;
    }
    
    public void setDiscardingThreshold(final int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }
    
    public int getNumberOfElementsInQueue() {
        return this.blockingQueue.size();
    }
    
    public int getRemainingCapacity() {
        return this.blockingQueue.remainingCapacity();
    }
    
    public void addAppender(final Appender<E> newAppender) {
        if (this.appenderCount == 0) {
            ++this.appenderCount;
            this.addInfo("Attaching appender named [" + newAppender.getName() + "] to AsyncAppender.");
            this.aai.addAppender(newAppender);
        }
        else {
            this.addWarn("One and only one appender may be attached to AsyncAppender.");
            this.addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
        }
    }
    
    public Iterator<Appender<E>> iteratorForAppenders() {
        return this.aai.iteratorForAppenders();
    }
    
    public Appender<E> getAppender(final String name) {
        return this.aai.getAppender(name);
    }
    
    public boolean isAttached(final Appender<E> eAppender) {
        return this.aai.isAttached(eAppender);
    }
    
    public void detachAndStopAllAppenders() {
        this.aai.detachAndStopAllAppenders();
    }
    
    public boolean detachAppender(final Appender<E> eAppender) {
        return this.aai.detachAppender(eAppender);
    }
    
    public boolean detachAppender(final String name) {
        return this.aai.detachAppender(name);
    }
    
    class Worker extends Thread
    {
        public void run() {
            final AsyncAppenderBase<E> parent = AsyncAppenderBase.this;
            final AppenderAttachableImpl<E> aai = parent.aai;
            while (parent.isStarted()) {
                try {
                    final E e = parent.blockingQueue.take();
                    aai.appendLoopOnAppenders(e);
                    continue;
                }
                catch (InterruptedException ie) {}
                break;
            }
            AsyncAppenderBase.this.addInfo("Worker thread will flush remaining events before exiting. ");
            for (final E e2 : parent.blockingQueue) {
                aai.appendLoopOnAppenders(e2);
            }
            aai.detachAndStopAllAppenders();
        }
    }
}

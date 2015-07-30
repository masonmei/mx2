// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.read;

import com.newrelic.agent.deps.ch.qos.logback.core.helpers.CyclicBuffer;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;

public class CyclicBufferAppender<E> extends AppenderBase<E>
{
    CyclicBuffer<E> cb;
    int maxSize;
    
    public CyclicBufferAppender() {
        this.maxSize = 512;
    }
    
    public void start() {
        this.cb = new CyclicBuffer<E>(this.maxSize);
        super.start();
    }
    
    public void stop() {
        this.cb = null;
        super.stop();
    }
    
    protected void append(final E eventObject) {
        if (!this.isStarted()) {
            return;
        }
        this.cb.add(eventObject);
    }
    
    public int getLength() {
        if (this.isStarted()) {
            return this.cb.length();
        }
        return 0;
    }
    
    public Object get(final int i) {
        if (this.isStarted()) {
            return this.cb.get(i);
        }
        return null;
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
}

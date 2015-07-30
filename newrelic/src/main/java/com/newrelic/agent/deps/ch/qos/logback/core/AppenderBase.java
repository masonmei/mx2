// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.Filter;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.WarnStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterAttachableImpl;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class AppenderBase<E> extends ContextAwareBase implements Appender<E>
{
    protected boolean started;
    private boolean guard;
    protected String name;
    private FilterAttachableImpl<E> fai;
    private int statusRepeatCount;
    private int exceptionCount;
    static final int ALLOWED_REPEATS = 5;
    
    public AppenderBase() {
        this.started = false;
        this.guard = false;
        this.fai = new FilterAttachableImpl<E>();
        this.statusRepeatCount = 0;
        this.exceptionCount = 0;
    }
    
    public String getName() {
        return this.name;
    }
    
    public synchronized void doAppend(final E eventObject) {
        if (this.guard) {
            return;
        }
        try {
            this.guard = true;
            if (!this.started) {
                if (this.statusRepeatCount++ < 5) {
                    this.addStatus(new WarnStatus("Attempted to append to non started appender [" + this.name + "].", this));
                }
                return;
            }
            if (this.getFilterChainDecision(eventObject) == FilterReply.DENY) {
                return;
            }
            this.append(eventObject);
        }
        catch (Exception e) {
            if (this.exceptionCount++ < 5) {
                this.addError("Appender [" + this.name + "] failed to append.", e);
            }
        }
        finally {
            this.guard = false;
        }
    }
    
    protected abstract void append(final E p0);
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void start() {
        this.started = true;
    }
    
    public void stop() {
        this.started = false;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public String toString() {
        return this.getClass().getName() + "[" + this.name + "]";
    }
    
    public void addFilter(final Filter<E> newFilter) {
        this.fai.addFilter(newFilter);
    }
    
    public void clearAllFilters() {
        this.fai.clearAllFilters();
    }
    
    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return this.fai.getCopyOfAttachedFiltersList();
    }
    
    public FilterReply getFilterChainDecision(final E event) {
        return this.fai.getFilterChainDecision(event);
    }
}

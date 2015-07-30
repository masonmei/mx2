// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.pattern;

import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;

public abstract class DynamicConverter<E> extends FormattingConverter<E> implements LifeCycle, ContextAware
{
    ContextAwareBase cab;
    private List<String> optionList;
    protected boolean started;
    
    public DynamicConverter() {
        this.cab = new ContextAwareBase(this);
        this.started = false;
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
    
    public void setOptionList(final List<String> optionList) {
        this.optionList = optionList;
    }
    
    public String getFirstOption() {
        if (this.optionList == null || this.optionList.size() == 0) {
            return null;
        }
        return this.optionList.get(0);
    }
    
    protected List<String> getOptionList() {
        return this.optionList;
    }
    
    public void setContext(final Context context) {
        this.cab.setContext(context);
    }
    
    public Context getContext() {
        return this.cab.getContext();
    }
    
    public void addStatus(final Status status) {
        this.cab.addStatus(status);
    }
    
    public void addInfo(final String msg) {
        this.cab.addInfo(msg);
    }
    
    public void addInfo(final String msg, final Throwable ex) {
        this.cab.addInfo(msg, ex);
    }
    
    public void addWarn(final String msg) {
        this.cab.addWarn(msg);
    }
    
    public void addWarn(final String msg, final Throwable ex) {
        this.cab.addWarn(msg, ex);
    }
    
    public void addError(final String msg) {
        this.cab.addError(msg);
    }
    
    public void addError(final String msg, final Throwable ex) {
        this.cab.addError(msg, ex);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.status.ErrorStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.status.WarnStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.status.InfoStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;

public class ContextAwareImpl implements ContextAware
{
    private int noContextWarning;
    protected Context context;
    final Object origin;
    
    public ContextAwareImpl(final Object origin) {
        this.noContextWarning = 0;
        this.origin = origin;
    }
    
    protected Object getOrigin() {
        return this.origin;
    }
    
    public void setContext(final Context context) {
        if (this.context == null) {
            this.context = context;
        }
        else if (this.context != context) {
            throw new IllegalStateException("Context has been already set");
        }
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public StatusManager getStatusManager() {
        if (this.context == null) {
            return null;
        }
        return this.context.getStatusManager();
    }
    
    public void addStatus(final Status status) {
        if (this.context == null) {
            if (this.noContextWarning++ == 0) {
                System.out.println("LOGBACK: No context given for " + this);
            }
            return;
        }
        final StatusManager sm = this.context.getStatusManager();
        if (sm != null) {
            sm.add(status);
        }
    }
    
    public void addInfo(final String msg) {
        this.addStatus(new InfoStatus(msg, this.getOrigin()));
    }
    
    public void addInfo(final String msg, final Throwable ex) {
        this.addStatus(new InfoStatus(msg, this.getOrigin(), ex));
    }
    
    public void addWarn(final String msg) {
        this.addStatus(new WarnStatus(msg, this.getOrigin()));
    }
    
    public void addWarn(final String msg, final Throwable ex) {
        this.addStatus(new WarnStatus(msg, this.getOrigin(), ex));
    }
    
    public void addError(final String msg) {
        this.addStatus(new ErrorStatus(msg, this.getOrigin()));
    }
    
    public void addError(final String msg, final Throwable ex) {
        this.addStatus(new ErrorStatus(msg, this.getOrigin(), ex));
    }
}

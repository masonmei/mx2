// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.status;

import java.util.Iterator;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.util.StatusPrinter;
import java.io.PrintStream;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

abstract class OnPrintStreamStatusListenerBase extends ContextAwareBase implements StatusListener, LifeCycle
{
    boolean isStarted;
    static final long DEFAULT_RESTROSPECTIVE = 300L;
    long retrospective;
    
    OnPrintStreamStatusListenerBase() {
        this.isStarted = false;
        this.retrospective = 300L;
    }
    
    protected abstract PrintStream getPrintStream();
    
    private void print(final Status status) {
        final StringBuilder sb = new StringBuilder();
        StatusPrinter.buildStr(sb, "", status);
        this.getPrintStream().print(sb);
    }
    
    public void addStatusEvent(final Status status) {
        if (!this.isStarted) {
            return;
        }
        this.print(status);
    }
    
    private void retrospectivePrint() {
        if (this.context == null) {
            return;
        }
        final long now = System.currentTimeMillis();
        final StatusManager sm = this.context.getStatusManager();
        final List<Status> statusList = sm.getCopyOfStatusList();
        for (final Status status : statusList) {
            final long timestamp = status.getDate();
            if (now - timestamp < this.retrospective) {
                this.print(status);
            }
        }
    }
    
    public void start() {
        this.isStarted = true;
        if (this.retrospective > 0L) {
            this.retrospectivePrint();
        }
    }
    
    public void setRetrospective(final long retrospective) {
        this.retrospective = retrospective;
    }
    
    public long getRetrospective() {
        return this.retrospective;
    }
    
    public void stop() {
        this.isStarted = false;
    }
    
    public boolean isStarted() {
        return this.isStarted;
    }
}

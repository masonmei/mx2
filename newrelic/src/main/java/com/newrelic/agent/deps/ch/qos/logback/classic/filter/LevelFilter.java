// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.filter;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.AbstractMatcherFilter;

public class LevelFilter extends AbstractMatcherFilter<ILoggingEvent>
{
    Level level;
    
    public FilterReply decide(final ILoggingEvent event) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (event.getLevel().equals(this.level)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }
    
    public void setLevel(final Level level) {
        this.level = level;
    }
    
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}

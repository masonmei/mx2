// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.filter;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.Filter;

public class ThresholdFilter extends Filter<ILoggingEvent>
{
    Level level;
    
    public FilterReply decide(final ILoggingEvent event) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (event.getLevel().isGreaterOrEqual(this.level)) {
            return FilterReply.NEUTRAL;
        }
        return FilterReply.DENY;
    }
    
    public void setLevel(final String level) {
        this.level = Level.toLevel(level);
    }
    
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}

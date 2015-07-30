// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.turbo;

import com.newrelic.agent.deps.org.slf4j.MDC;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import com.newrelic.agent.deps.org.slf4j.Marker;

public class MDCFilter extends MatchingFilter
{
    String MDCKey;
    String value;
    
    public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format, final Object[] params, final Throwable t) {
        if (this.MDCKey == null) {
            return FilterReply.NEUTRAL;
        }
        final String value = MDC.get(this.MDCKey);
        if (this.value.equals(value)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public void setMDCKey(final String MDCKey) {
        this.MDCKey = MDCKey;
    }
}

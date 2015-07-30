// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.turbo;

import com.newrelic.agent.deps.org.slf4j.MarkerFactory;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import com.newrelic.agent.deps.org.slf4j.Marker;

public class MarkerFilter extends MatchingFilter
{
    Marker markerToMatch;
    
    public void start() {
        if (this.markerToMatch != null) {
            super.start();
        }
        else {
            this.addError("The marker property must be set for [" + this.getName() + "]");
        }
    }
    
    public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format, final Object[] params, final Throwable t) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (marker == null) {
            return this.onMismatch;
        }
        if (marker.contains(this.markerToMatch)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }
    
    public void setMarker(final String markerStr) {
        if (markerStr != null) {
            this.markerToMatch = MarkerFactory.getMarker(markerStr);
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.org.slf4j.Marker;
import java.util.logging.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.Filter;

class FineFilter extends Filter<ILoggingEvent>
{
    private static FineFilter instance;
    private volatile Level javaLevel;
    private final Marker markerToMatch;
    private final Marker markerToFail;
    
    public static FineFilter getFineFilter() {
        if (FineFilter.instance == null) {
            FineFilter.instance = new FineFilter();
        }
        return FineFilter.instance;
    }
    
    private FineFilter() {
        this.markerToMatch = LogbackMarkers.FINE_MARKER;
        this.markerToFail = LogbackMarkers.FINER_MARKER;
        this.javaLevel = Level.INFO;
    }
    
    public FilterReply decide(final ILoggingEvent pEvent) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (Level.FINE.equals(this.javaLevel)) {
            final Marker marker = pEvent.getMarker();
            if (marker == null) {
                return FilterReply.NEUTRAL;
            }
            if (marker.contains(this.markerToMatch)) {
                return FilterReply.ACCEPT;
            }
            if (marker.contains(this.markerToFail)) {
                return FilterReply.DENY;
            }
        }
        return FilterReply.NEUTRAL;
    }
    
    public boolean isEnabledFor(final Level pLevel) {
        return this.javaLevel.intValue() <= pLevel.intValue();
    }
    
    public void setLevel(final Level level) {
        this.javaLevel = level;
    }
    
    public Level getLevel() {
        return this.javaLevel;
    }
    
    public void start() {
        if (this.javaLevel != null) {
            super.start();
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ClassicConverter;

public class MarkerLevelConverter extends ClassicConverter
{
    public String convert(final ILoggingEvent pEvent) {
        final Marker marker = pEvent.getMarker();
        if (marker == null) {
            return pEvent.getLevel().toString();
        }
        return marker.getName();
    }
}

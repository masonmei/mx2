// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public class MarkerConverter extends ClassicConverter
{
    private static String EMPTY;
    
    public String convert(final ILoggingEvent le) {
        final Marker marker = le.getMarker();
        if (marker == null) {
            return MarkerConverter.EMPTY;
        }
        return marker.toString();
    }
    
    static {
        MarkerConverter.EMPTY = "";
    }
}

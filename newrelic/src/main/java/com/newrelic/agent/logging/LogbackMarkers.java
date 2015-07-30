// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.deps.org.slf4j.MarkerFactory;
import com.newrelic.agent.deps.org.slf4j.Marker;

class LogbackMarkers
{
    private static String FINE_STR;
    private static String FINER_STR;
    private static String FINEST_STR;
    public static final Marker FINE_MARKER;
    public static final Marker FINER_MARKER;
    public static final Marker FINEST_MARKER;
    
    static {
        LogbackMarkers.FINE_STR = "FINE";
        LogbackMarkers.FINER_STR = "FINER";
        LogbackMarkers.FINEST_STR = "FINEST";
        FINE_MARKER = MarkerFactory.getMarker(LogbackMarkers.FINE_STR);
        FINER_MARKER = MarkerFactory.getMarker(LogbackMarkers.FINER_STR);
        FINEST_MARKER = MarkerFactory.getMarker(LogbackMarkers.FINEST_STR);
    }
}

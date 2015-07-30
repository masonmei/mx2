// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;

enum LogbackLevel
{
    OFF("off", Level.OFF, java.util.logging.Level.OFF, (Marker)null), 
    ALL("all", Level.ALL, java.util.logging.Level.ALL, (Marker)null), 
    FATAL("fatal", Level.ERROR, java.util.logging.Level.SEVERE, (Marker)null), 
    SEVERE("severe", Level.ERROR, java.util.logging.Level.SEVERE, (Marker)null), 
    ERROR("error", Level.ERROR, java.util.logging.Level.SEVERE, (Marker)null), 
    WARN("warn", Level.WARN, java.util.logging.Level.WARNING, (Marker)null), 
    WARNING("warning", Level.WARN, java.util.logging.Level.WARNING, (Marker)null), 
    INFO("info", Level.INFO, java.util.logging.Level.INFO, (Marker)null), 
    CONFIG("config", Level.INFO, java.util.logging.Level.CONFIG, (Marker)null), 
    FINE("fine", Level.DEBUG, java.util.logging.Level.FINE, LogbackMarkers.FINE_MARKER), 
    FINER("finer", Level.DEBUG, java.util.logging.Level.FINER, LogbackMarkers.FINER_MARKER), 
    FINEST("finest", Level.TRACE, java.util.logging.Level.FINEST, LogbackMarkers.FINEST_MARKER), 
    DEBUG("debug", Level.DEBUG, java.util.logging.Level.FINE, (Marker)null), 
    TRACE("trace", Level.TRACE, java.util.logging.Level.FINEST, (Marker)null);
    
    private final String name;
    private final Level logbackLevel;
    private final java.util.logging.Level javaLevel;
    private final Marker marker;
    private static final Map<String, LogbackLevel> CONVERSION;
    private static final Map<java.util.logging.Level, LogbackLevel> JAVA_TO_LOGBACK;
    
    private LogbackLevel(final String pName, final Level pLogbackLevel, final java.util.logging.Level pJavaLevel, final Marker pMarker) {
        this.name = pName;
        this.logbackLevel = pLogbackLevel;
        this.javaLevel = pJavaLevel;
        this.marker = pMarker;
    }
    
    public Marker getMarker() {
        return this.marker;
    }
    
    public Level getLogbackLevel() {
        return this.logbackLevel;
    }
    
    public java.util.logging.Level getJavaLevel() {
        return this.javaLevel;
    }
    
    public static LogbackLevel getLevel(final String pName, final LogbackLevel pDefault) {
        final LogbackLevel level = LogbackLevel.CONVERSION.get(pName);
        return (level == null) ? pDefault : level;
    }
    
    public static LogbackLevel getLevel(final java.util.logging.Level pName) {
        return LogbackLevel.JAVA_TO_LOGBACK.get(pName);
    }
    
    static {
        CONVERSION = new HashMap<String, LogbackLevel>();
        JAVA_TO_LOGBACK = new HashMap<java.util.logging.Level, LogbackLevel>();
        final LogbackLevel[] arr$;
        final LogbackLevel[] levels = arr$ = values();
        for (final LogbackLevel level : arr$) {
            LogbackLevel.CONVERSION.put(level.name, level);
        }
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.ALL, LogbackLevel.ALL);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.FINER, LogbackLevel.FINER);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.FINEST, LogbackLevel.FINEST);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.FINE, LogbackLevel.FINE);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.WARNING, LogbackLevel.WARNING);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.SEVERE, LogbackLevel.SEVERE);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.CONFIG, LogbackLevel.CONFIG);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.INFO, LogbackLevel.INFO);
        LogbackLevel.JAVA_TO_LOGBACK.put(java.util.logging.Level.OFF, LogbackLevel.OFF);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j;

public interface Logger
{
    public static final String ROOT_LOGGER_NAME = "ROOT";
    
    String getName();
    
    boolean isTraceEnabled();
    
    void trace(String p0);
    
    void trace(String p0, Object p1);
    
    void trace(String p0, Object p1, Object p2);
    
    void trace(String p0, Object... p1);
    
    void trace(String p0, Throwable p1);
    
    boolean isTraceEnabled(Marker p0);
    
    void trace(Marker p0, String p1);
    
    void trace(Marker p0, String p1, Object p2);
    
    void trace(Marker p0, String p1, Object p2, Object p3);
    
    void trace(Marker p0, String p1, Object... p2);
    
    void trace(Marker p0, String p1, Throwable p2);
    
    boolean isDebugEnabled();
    
    void debug(String p0);
    
    void debug(String p0, Object p1);
    
    void debug(String p0, Object p1, Object p2);
    
    void debug(String p0, Object... p1);
    
    void debug(String p0, Throwable p1);
    
    boolean isDebugEnabled(Marker p0);
    
    void debug(Marker p0, String p1);
    
    void debug(Marker p0, String p1, Object p2);
    
    void debug(Marker p0, String p1, Object p2, Object p3);
    
    void debug(Marker p0, String p1, Object... p2);
    
    void debug(Marker p0, String p1, Throwable p2);
    
    boolean isInfoEnabled();
    
    void info(String p0);
    
    void info(String p0, Object p1);
    
    void info(String p0, Object p1, Object p2);
    
    void info(String p0, Object... p1);
    
    void info(String p0, Throwable p1);
    
    boolean isInfoEnabled(Marker p0);
    
    void info(Marker p0, String p1);
    
    void info(Marker p0, String p1, Object p2);
    
    void info(Marker p0, String p1, Object p2, Object p3);
    
    void info(Marker p0, String p1, Object... p2);
    
    void info(Marker p0, String p1, Throwable p2);
    
    boolean isWarnEnabled();
    
    void warn(String p0);
    
    void warn(String p0, Object p1);
    
    void warn(String p0, Object... p1);
    
    void warn(String p0, Object p1, Object p2);
    
    void warn(String p0, Throwable p1);
    
    boolean isWarnEnabled(Marker p0);
    
    void warn(Marker p0, String p1);
    
    void warn(Marker p0, String p1, Object p2);
    
    void warn(Marker p0, String p1, Object p2, Object p3);
    
    void warn(Marker p0, String p1, Object... p2);
    
    void warn(Marker p0, String p1, Throwable p2);
    
    boolean isErrorEnabled();
    
    void error(String p0);
    
    void error(String p0, Object p1);
    
    void error(String p0, Object p1, Object p2);
    
    void error(String p0, Object... p1);
    
    void error(String p0, Throwable p1);
    
    boolean isErrorEnabled(Marker p0);
    
    void error(Marker p0, String p1);
    
    void error(Marker p0, String p1, Object p2);
    
    void error(Marker p0, String p1, Object p2, Object p3);
    
    void error(Marker p0, String p1, Object... p2);
    
    void error(Marker p0, String p1, Throwable p2);
}

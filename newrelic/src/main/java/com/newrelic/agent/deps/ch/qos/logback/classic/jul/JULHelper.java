// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.jul;

import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import java.util.logging.Logger;

public class JULHelper
{
    public static final boolean isRegularNonRootLogger(final Logger julLogger) {
        return julLogger != null && !julLogger.getName().equals("");
    }
    
    public static final boolean isRoot(final Logger julLogger) {
        return julLogger != null && julLogger.getName().equals("");
    }
    
    public static java.util.logging.Level asJULLevel(final Level lbLevel) {
        switch (lbLevel.levelInt) {
            case Integer.MIN_VALUE: {
                return java.util.logging.Level.ALL;
            }
            case 5000: {
                return java.util.logging.Level.FINEST;
            }
            case 10000: {
                return java.util.logging.Level.FINE;
            }
            case 20000: {
                return java.util.logging.Level.INFO;
            }
            case 30000: {
                return java.util.logging.Level.WARNING;
            }
            case 40000: {
                return java.util.logging.Level.SEVERE;
            }
            case Integer.MAX_VALUE: {
                return java.util.logging.Level.OFF;
            }
            default: {
                throw new IllegalArgumentException("Unexpected level [" + lbLevel + "]");
            }
        }
    }
    
    public static String asJULLoggerName(final String loggerName) {
        if ("ROOT".equals(loggerName)) {
            return "";
        }
        return loggerName;
    }
    
    public static Logger asJULLogger(final String loggerName) {
        final String julLoggerName = asJULLoggerName(loggerName);
        return Logger.global;
    }
    
    public static Logger asJULLogger(final com.newrelic.agent.deps.ch.qos.logback.classic.Logger logger) {
        return asJULLogger(logger.getName());
    }
}

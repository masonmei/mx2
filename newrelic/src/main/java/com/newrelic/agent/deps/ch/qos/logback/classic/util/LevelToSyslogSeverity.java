// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public class LevelToSyslogSeverity
{
    public static int convert(final ILoggingEvent event) {
        final Level level = event.getLevel();
        switch (level.levelInt) {
            case 40000: {
                return 3;
            }
            case 30000: {
                return 4;
            }
            case 20000: {
                return 6;
            }
            case 5000:
            case 10000: {
                return 7;
            }
            default: {
                throw new IllegalArgumentException("Level " + level + " is not a valid level for a printing method");
            }
        }
    }
}

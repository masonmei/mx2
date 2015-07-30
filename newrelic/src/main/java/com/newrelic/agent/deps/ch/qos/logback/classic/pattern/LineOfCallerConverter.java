// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public class LineOfCallerConverter extends ClassicConverter
{
    public String convert(final ILoggingEvent le) {
        final StackTraceElement[] cda = le.getCallerData();
        if (cda != null && cda.length > 0) {
            return Integer.toString(cda[0].getLineNumber());
        }
        return "?";
    }
}

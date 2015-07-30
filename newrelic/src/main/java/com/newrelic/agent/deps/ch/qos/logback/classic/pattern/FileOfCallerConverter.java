// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public class FileOfCallerConverter extends ClassicConverter
{
    public String convert(final ILoggingEvent le) {
        final StackTraceElement[] cda = le.getCallerData();
        if (cda != null && cda.length > 0) {
            return cda[0].getFileName();
        }
        return "?";
    }
}

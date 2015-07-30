// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ClassicConverter;

public class ThreadIdLogbackConverter extends ClassicConverter
{
    public String convert(final ILoggingEvent event) {
        try {
            final long theId = Thread.currentThread().getId();
            return Long.toString(theId);
        }
        catch (Exception e) {
            return null;
        }
    }
}

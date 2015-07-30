// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;

public class ThreadConverter extends ClassicConverter
{
    public String convert(final ILoggingEvent event) {
        return event.getThreadName();
    }
}

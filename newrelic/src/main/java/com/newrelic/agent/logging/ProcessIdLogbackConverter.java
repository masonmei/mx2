// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import java.lang.management.ManagementFactory;
import com.newrelic.agent.deps.ch.qos.logback.classic.pattern.ClassicConverter;

public class ProcessIdLogbackConverter extends ClassicConverter
{
    private final String pid;
    
    public ProcessIdLogbackConverter() {
        this.pid = Integer.toString(getProcessId());
    }
    
    private static int getProcessId() {
        final String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        final String[] split = runtimeName.split("@");
        if (split.length > 1) {
            return Integer.parseInt(split[0]);
        }
        return 0;
    }
    
    public String convert(final ILoggingEvent event) {
        try {
            return this.pid;
        }
        catch (Exception e) {
            return null;
        }
    }
}

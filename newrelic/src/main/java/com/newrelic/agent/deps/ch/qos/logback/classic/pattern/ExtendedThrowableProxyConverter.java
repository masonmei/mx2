// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ThrowableProxyUtil;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.StackTraceElementProxy;

public class ExtendedThrowableProxyConverter extends ThrowableProxyConverter
{
    protected void extraData(final StringBuilder builder, final StackTraceElementProxy step) {
        if (step != null) {
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
        }
    }
    
    protected void prepareLoggingEvent(final ILoggingEvent event) {
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.net;

import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggingEventVO;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggingEvent;
import java.io.Serializable;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.PreSerializationTransformer;

public class LoggingEventPreSerializationTransformer implements PreSerializationTransformer<ILoggingEvent>
{
    public Serializable transform(final ILoggingEvent event) {
        if (event == null) {
            return null;
        }
        if (event instanceof LoggingEvent) {
            return LoggingEventVO.build(event);
        }
        if (event instanceof LoggingEventVO) {
            return (LoggingEventVO)event;
        }
        throw new IllegalArgumentException("Unsupported type " + event.getClass().getName());
    }
}

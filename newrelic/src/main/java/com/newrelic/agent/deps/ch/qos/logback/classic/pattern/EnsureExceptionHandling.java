// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import com.newrelic.agent.deps.ch.qos.logback.core.pattern.ConverterUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.Converter;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.PostCompileProcessor;

public class EnsureExceptionHandling implements PostCompileProcessor<ILoggingEvent>
{
    public void process(final Converter<ILoggingEvent> head) {
        if (head == null) {
            throw new IllegalArgumentException("cannot process empty chain");
        }
        if (!this.chainHandlesThrowable(head)) {
            final Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
            final Converter<ILoggingEvent> exConverter = new ExtendedThrowableProxyConverter();
            tail.setNext(exConverter);
        }
    }
    
    public boolean chainHandlesThrowable(final Converter head) {
        for (Converter c = head; c != null; c = c.getNext()) {
            if (c instanceof ThrowableHandlingConverter) {
                return true;
            }
        }
        return false;
    }
}

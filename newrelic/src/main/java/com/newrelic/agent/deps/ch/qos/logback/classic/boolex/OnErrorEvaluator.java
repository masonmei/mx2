// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.boolex;

import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EvaluationException;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluatorBase;

public class OnErrorEvaluator extends EventEvaluatorBase<ILoggingEvent>
{
    public boolean evaluate(final ILoggingEvent event) throws NullPointerException, EvaluationException {
        return event.getLevel().levelInt >= 40000;
    }
}

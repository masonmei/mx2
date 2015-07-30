// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern.color;

import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class HighlightingCompositeConverter extends ForegroundCompositeConverterBase<ILoggingEvent>
{
    protected String getForegroundColorCode(final ILoggingEvent event) {
        final Level level = event.getLevel();
        switch (level.toInt()) {
            case 40000: {
                return "1;31";
            }
            case 30000: {
                return "31";
            }
            case 20000: {
                return "34";
            }
            default: {
                return "39";
            }
        }
    }
}

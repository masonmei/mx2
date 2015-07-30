// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.deps.ch.qos.logback.core.Layout;
import com.newrelic.agent.deps.ch.qos.logback.classic.PatternLayout;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

class CustomPatternLogbackEncoder extends PatternLayoutEncoderBase<ILoggingEvent>
{
    public CustomPatternLogbackEncoder(final String pPattern) {
        this.setPattern(pPattern);
    }
    
    public void start() {
        final PatternLayout patternLayout = new CustomPatternLogbackLayout(this.getPattern());
        patternLayout.setContext(this.context);
        patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
        patternLayout.start();
        this.layout = (Layout<E>)patternLayout;
        super.start();
    }
}

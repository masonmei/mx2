// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.encoder;

import com.newrelic.agent.deps.ch.qos.logback.core.Layout;
import com.newrelic.agent.deps.ch.qos.logback.classic.PatternLayout;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

public class PatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent>
{
    public void start() {
        final PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(this.context);
        patternLayout.setPattern(this.getPattern());
        patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
        patternLayout.start();
        this.layout = (Layout<E>)patternLayout;
        super.start();
    }
}

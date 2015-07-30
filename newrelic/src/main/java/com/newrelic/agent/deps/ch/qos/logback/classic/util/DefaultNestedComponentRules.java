// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

import com.newrelic.agent.deps.ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.EvaluatorFilter;
import com.newrelic.agent.deps.ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import com.newrelic.agent.deps.ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.newrelic.agent.deps.ch.qos.logback.classic.PatternLayout;
import com.newrelic.agent.deps.ch.qos.logback.core.AppenderBase;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;

public class DefaultNestedComponentRules
{
    public static void addDefaultNestedComponentRegistryRules(final DefaultNestedComponentRegistry registry) {
        registry.add(AppenderBase.class, "layout", PatternLayout.class);
        registry.add(UnsynchronizedAppenderBase.class, "layout", PatternLayout.class);
        registry.add(AppenderBase.class, "encoder", PatternLayoutEncoder.class);
        registry.add(UnsynchronizedAppenderBase.class, "encoder", PatternLayoutEncoder.class);
        registry.add(EvaluatorFilter.class, "evaluator", JaninoEventEvaluator.class);
    }
}

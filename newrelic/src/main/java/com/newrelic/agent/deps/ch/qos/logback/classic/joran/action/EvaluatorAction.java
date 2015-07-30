// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.joran.action;

import com.newrelic.agent.deps.ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.action.AbstractEventEvaluatorAction;

public class EvaluatorAction extends AbstractEventEvaluatorAction
{
    protected String defaultClassName() {
        return JaninoEventEvaluator.class.getName();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.filter;

import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EvaluationException;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.ch.qos.logback.core.boolex.EventEvaluator;

public class EvaluatorFilter<E> extends AbstractMatcherFilter<E>
{
    EventEvaluator<E> evaluator;
    
    public void start() {
        if (this.evaluator != null) {
            super.start();
        }
        else {
            this.addError("No evaluator set for filter " + this.getName());
        }
    }
    
    public EventEvaluator<E> getEvaluator() {
        return this.evaluator;
    }
    
    public void setEvaluator(final EventEvaluator<E> evaluator) {
        this.evaluator = evaluator;
    }
    
    public FilterReply decide(final E event) {
        if (!this.isStarted() || !this.evaluator.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        try {
            if (this.evaluator.evaluate(event)) {
                return this.onMatch;
            }
            return this.onMismatch;
        }
        catch (EvaluationException e) {
            this.addError("Evaluator " + this.evaluator.getName() + " threw an exception", e);
            return FilterReply.NEUTRAL;
        }
    }
}

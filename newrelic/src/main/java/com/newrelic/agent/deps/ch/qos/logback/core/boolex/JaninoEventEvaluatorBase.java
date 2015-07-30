// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.boolex;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.janino.ScriptEvaluator;

public abstract class JaninoEventEvaluatorBase<E> extends EventEvaluatorBase<E>
{
    static Class EXPRESSION_TYPE;
    static Class[] THROWN_EXCEPTIONS;
    public static final int ERROR_THRESHOLD = 4;
    private String expression;
    ScriptEvaluator scriptEvaluator;
    private int errorCount;
    protected List<Matcher> matcherList;
    
    public JaninoEventEvaluatorBase() {
        this.errorCount = 0;
        this.matcherList = new ArrayList<Matcher>();
    }
    
    protected abstract String getDecoratedExpression();
    
    protected abstract String[] getParameterNames();
    
    protected abstract Class[] getParameterTypes();
    
    protected abstract Object[] getParameterValues(final E p0);
    
    public void start() {
        try {
            assert this.context != null;
            this.scriptEvaluator = new ScriptEvaluator(this.getDecoratedExpression(), JaninoEventEvaluatorBase.EXPRESSION_TYPE, this.getParameterNames(), this.getParameterTypes(), JaninoEventEvaluatorBase.THROWN_EXCEPTIONS);
            super.start();
        }
        catch (Exception e) {
            this.addError("Could not start evaluator with expression [" + this.expression + "]", e);
        }
    }
    
    public boolean evaluate(final E event) throws EvaluationException {
        if (!this.isStarted()) {
            throw new IllegalStateException("Evaluator [" + this.name + "] was called in stopped state");
        }
        try {
            final Boolean result = (Boolean)this.scriptEvaluator.evaluate(this.getParameterValues(event));
            return result;
        }
        catch (Exception ex) {
            ++this.errorCount;
            if (this.errorCount >= 4) {
                this.stop();
            }
            throw new EvaluationException("Evaluator [" + this.name + "] caused an exception", ex);
        }
    }
    
    public String getExpression() {
        return this.expression;
    }
    
    public void setExpression(final String expression) {
        this.expression = expression;
    }
    
    public void addMatcher(final Matcher matcher) {
        this.matcherList.add(matcher);
    }
    
    public List getMatcherList() {
        return this.matcherList;
    }
    
    static {
        JaninoEventEvaluatorBase.EXPRESSION_TYPE = Boolean.TYPE;
        (JaninoEventEvaluatorBase.THROWN_EXCEPTIONS = new Class[1])[0] = EvaluationException.class;
    }
}

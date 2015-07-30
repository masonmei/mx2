// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.errors;

import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.PointCutConfiguration;

public final class ExceptionHandlerPointCut extends AbstractExceptionHandlerPointCut
{
    private final int exceptionArgumentIndex;
    
    public ExceptionHandlerPointCut(final ExceptionHandlerSignature sig) {
        super(new PointCutConfiguration("exception_handler"), sig.getClassMatcher(), sig.getMethodMatcher());
        this.exceptionArgumentIndex = sig.getExceptionArgumentIndex();
    }
    
    protected Throwable getThrowable(final ClassMethodSignature sig, final Object[] args) {
        if (this.exceptionArgumentIndex >= 0) {
            return (Throwable)args[this.exceptionArgumentIndex];
        }
        for (int i = 0; i < args.length; ++i) {
            if (args[i] instanceof Throwable) {
                return (Throwable)args[i];
            }
        }
        return null;
    }
}

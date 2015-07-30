// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.Transaction;

public final class IgnoreTransactionTracerFactory extends AbstractTracerFactory
{
    public static final String TRACER_FACTORY_NAME;
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        transaction.setIgnore(true);
        return new MethodExitTracerNoSkip(sig, transaction) {
            protected void doFinish(final int opcode, final Object returnValue) {
            }
        };
    }
    
    static {
        TRACER_FACTORY_NAME = IgnoreTransactionTracerFactory.class.getName();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.Transaction;

public abstract class MethodExitTracer extends MethodExitTracerNoSkip implements SkipTracer
{
    public MethodExitTracer(final ClassMethodSignature signature, final Transaction transaction) {
        super(signature, transaction);
    }
    
    public final void childTracerFinished(final Tracer child) {
        if (Agent.LOG.isLoggable(Level.FINER)) {
            final String msg = MessageFormat.format("childTracerFinished called on {0} with child {1}", this.getClass().getName(), child.getClass().getName());
            Agent.LOG.finer(msg);
        }
    }
}

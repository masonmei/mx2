// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.dispatchers.Dispatcher;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.Transaction;
import java.lang.reflect.Method;
import com.newrelic.agent.TracerService;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.TracerFactory;
import java.lang.reflect.InvocationHandler;

final class InvocationPoint implements InvocationHandler
{
    private final TracerFactory tracerFactory;
    private final ClassMethodSignature classMethodSignature;
    private final TracerService tracerService;
    private final boolean ignoreApdex;
    
    public InvocationPoint(final TracerService tracerService, final ClassMethodSignature classMethodSignature, final TracerFactory tracerFactory, final boolean ignoreApdex) {
        this.tracerService = tracerService;
        this.tracerFactory = tracerFactory;
        this.classMethodSignature = classMethodSignature;
        this.ignoreApdex = ignoreApdex;
    }
    
    public ClassMethodSignature getClassMethodSignature() {
        return this.classMethodSignature;
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        try {
            if (this.ignoreApdex) {
                final Transaction transaction = Transaction.getTransaction();
                if (transaction != null) {
                    final Dispatcher dispatcher = transaction.getDispatcher();
                    if (dispatcher != null) {
                        dispatcher.setIgnoreApdex(true);
                        if (Agent.LOG.isLoggable(Level.FINER)) {
                            final String msg = MessageFormat.format("Set Ignore apdex to \"{0}\"", true);
                            Agent.LOG.log(Level.FINER, msg, new Exception());
                        }
                    }
                }
            }
            Object t = this.tracerService.getTracer(this.tracerFactory, this.classMethodSignature, args[0], (Object[])args[1]);
            if (t == null) {
                t = null;
            }
            return t;
        }
        catch (Throwable t2) {
            Agent.LOG.log(Level.FINEST, "Tracer invocation error", t2);
            return null;
        }
    }
    
    public String toString() {
        return MessageFormat.format("{0} {1}", this.classMethodSignature);
    }
    
    public TracerFactory getTracerFactory() {
        return this.tracerFactory;
    }
    
    public static InvocationHandler getStacklessInvocationHandler(final ClassMethodSignature classMethodSignature, final EntryInvocationHandler tracerFactory) {
        return new StacklessInvocationPoint(classMethodSignature, tracerFactory);
    }
    
    public static InvocationHandler getInvocationPoint(final PointCutInvocationHandler invocationHandler, final TracerService tracerService, final ClassMethodSignature classMethodSignature, final boolean ignoreApdex) {
        if (invocationHandler instanceof EntryInvocationHandler) {
            return getStacklessInvocationHandler(classMethodSignature, (EntryInvocationHandler)invocationHandler);
        }
        if (invocationHandler instanceof TracerFactory) {
            return new InvocationPoint(tracerService, classMethodSignature.intern(), (TracerFactory)invocationHandler, ignoreApdex);
        }
        Agent.LOG.finest("Unable to create an invocation handler for " + invocationHandler);
        if (ignoreApdex) {
            return IgnoreApdexInvocationHandler.INVOCATION_HANDLER;
        }
        return NoOpInvocationHandler.INVOCATION_HANDLER;
    }
    
    private static final class StacklessInvocationPoint implements InvocationHandler
    {
        private final ClassMethodSignature classMethodSignature;
        private final EntryInvocationHandler tracerFactory;
        
        public StacklessInvocationPoint(final ClassMethodSignature classMethodSignature, final EntryInvocationHandler tracerFactory) {
            this.classMethodSignature = classMethodSignature;
            this.tracerFactory = tracerFactory;
        }
        
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            this.tracerFactory.handleInvocation(this.classMethodSignature, args[0], (Object[])args[1]);
            return null;
        }
    }
}

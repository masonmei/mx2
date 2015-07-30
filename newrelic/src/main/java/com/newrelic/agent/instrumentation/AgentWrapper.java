// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.TracerFactory;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.bridge.ExitTracer;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.logging.Level;
import java.lang.reflect.Method;
import com.newrelic.agent.Agent;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.IAgent;
import com.newrelic.agent.TracerService;
import java.lang.reflect.InvocationHandler;

public class AgentWrapper implements InvocationHandler
{
    public static final String CLASSLOADER_KEY = "CLASSLOADER";
    public static final String SUCCESSFUL_METHOD_INVOCATION = "s";
    public static final String UNSUCCESSFUL_METHOD_INVOCATION = "u";
    private final TracerService tracerService;
    private final IAgent agent;
    private final IAgentLogger logger;
    private final ClassTransformer classTransformer;
    
    private AgentWrapper(final ClassTransformer classTransformer) {
        this.tracerService = ServiceFactory.getTracerService();
        this.classTransformer = classTransformer;
        this.agent = ServiceFactory.getAgent();
        this.logger = Agent.LOG.getChildLogger("com.newrelic.agent.InvocationHandler");
    }
    
    public static AgentWrapper getAgentWrapper(final ClassTransformer classTransformer) {
        return new AgentWrapper(classTransformer);
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        if ("CLASSLOADER" == proxy) {
            return Agent.getClassLoader();
        }
        if (!this.agent.isEnabled()) {
            return NoOpInvocationHandler.INVOCATION_HANDLER;
        }
        try {
            if (proxy instanceof Class) {
                return this.createInvocationHandler(proxy, args);
            }
            if (proxy instanceof Integer) {
                final PointCutInvocationHandler invocationHandler = this.tracerService.getInvocationHandler((int)proxy);
                return invoke(invocationHandler, (String)args[0], (String)args[1], (String)args[2], args[3], (Object[])args[4]);
            }
            this.logger.log(Level.FINEST, "Unknown invocation type " + proxy);
        }
        catch (Throwable ex) {
            this.logger.log(Level.FINEST, "Error initializing invocation point", ex);
        }
        return NoOpInvocationHandler.INVOCATION_HANDLER;
    }
    
    private Object createInvocationHandler(final Object proxy, final Object[] args) {
        final boolean ignoreTransaction = (boolean)args[4];
        if (ignoreTransaction) {
            return IgnoreTransactionHandler.IGNORE_TRANSACTION_INVOCATION_HANDLER;
        }
        return this.classTransformer.evaluate((Class)proxy, this.tracerService, args[0], args[1], args[2], (boolean)args[3], args);
    }
    
    public static ExitTracer invoke(final PointCutInvocationHandler invocationHandler, final String className, final String methodName, final String methodDesc, final Object invocationTarget, final Object[] args) {
        final ClassMethodSignature classMethodSig = new ClassMethodSignature(className, methodName, methodDesc);
        if (invocationHandler instanceof EntryInvocationHandler) {
            final EntryInvocationHandler handler = (EntryInvocationHandler)invocationHandler;
            handler.handleInvocation(classMethodSig, invocationTarget, args);
            return null;
        }
        if (invocationHandler instanceof TracerFactory) {
            return (ExitTracer)ServiceFactory.getTracerService().getTracer((TracerFactory)invocationHandler, classMethodSig, invocationTarget, args);
        }
        return null;
    }
    
    private static class IgnoreTransactionHandler implements InvocationHandler
    {
        static final InvocationHandler IGNORE_TRANSACTION_INVOCATION_HANDLER;
        
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            final Transaction tx = Transaction.getTransaction();
            if (tx != null) {
                tx.setIgnore(true);
            }
            return NoOpInvocationHandler.INVOCATION_HANDLER;
        }
        
        static {
            IGNORE_TRANSACTION_INVOCATION_HANDLER = new IgnoreTransactionHandler();
        }
    }
}

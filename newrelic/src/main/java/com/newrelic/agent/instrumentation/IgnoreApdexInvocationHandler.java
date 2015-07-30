// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.dispatchers.Dispatcher;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.Transaction;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

class IgnoreApdexInvocationHandler implements InvocationHandler
{
    static final InvocationHandler INVOCATION_HANDLER;
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
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
        return null;
    }
    
    static {
        INVOCATION_HANDLER = new IgnoreApdexInvocationHandler();
    }
}

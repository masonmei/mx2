// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

import com.newrelic.agent.util.Strings;
import java.util.HashSet;
import com.newrelic.agent.bridge.TracedMethod;
import com.newrelic.agent.transaction.PriorityTransactionName;
import com.newrelic.agent.bridge.TransactionNamePriority;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import java.lang.reflect.Method;
import com.newrelic.agent.Transaction;
import java.util.Set;
import com.newrelic.agent.TransactionActivity;

public abstract class AbstractTracer implements Tracer
{
    private final TransactionActivity transactionActivity;
    private Set<String> rollupMetricNames;
    private Set<String> exclusiveRollupMetricNames;
    
    public AbstractTracer(final Transaction transaction) {
        this(transaction.getTransactionActivity());
    }
    
    public AbstractTracer(final TransactionActivity txa) {
        this.transactionActivity = txa;
    }
    
    public final Transaction getTransaction() {
        return this.transactionActivity.getTransaction();
    }
    
    public final TransactionActivity getTransactionActivity() {
        return this.transactionActivity;
    }
    
    protected Object getInvocationTarget() {
        return null;
    }
    
    public final Object invoke(final Object methodName, final Method method, final Object[] args) {
        try {
            if (args == null) {
                Agent.LOG.severe("Tracer.finish() was invoked with no arguments");
            }
            else if ("s" == methodName) {
                if (args.length == 2) {
                    this.finish((Integer)args[0], args[1]);
                }
                else {
                    Agent.LOG.severe(MessageFormat.format("Tracer.finish(int, Object) was invoked with {0} arguments(s)", args.length));
                }
            }
            else if ("u" == methodName) {
                if (args.length == 1) {
                    this.finish((Throwable)args[0]);
                }
                else {
                    Agent.LOG.severe(MessageFormat.format("Tracer.finish(Throwable) was invoked with {0} arguments(s)", args.length));
                }
            }
            else {
                Agent.LOG.severe(MessageFormat.format("Tracer.finish was invoked with an unknown method: {0}", methodName));
            }
        }
        catch (RetryException e) {
            return this.invoke(methodName, method, args);
        }
        catch (Throwable t) {
            if (Agent.LOG.isLoggable(Level.FINE)) {
                final String msg = MessageFormat.format("An error occurred finishing method tracer {0} for signature {1} : {2}", this.getClass().getName(), this.getClassMethodSignature(), t.toString());
                if (Agent.LOG.isLoggable(Level.FINEST)) {
                    Agent.LOG.log(Level.FINEST, msg, t);
                }
                else {
                    Agent.LOG.fine(msg);
                }
            }
        }
        return null;
    }
    
    public abstract ClassMethodSignature getClassMethodSignature();
    
    public boolean isChildHasStackTrace() {
        return false;
    }
    
    public void nameTransaction(final TransactionNamePriority priority) {
        try {
            final ClassMethodSignature classMethodSignature = this.getClassMethodSignature();
            final Object invocationTarget = this.getInvocationTarget();
            final String className = (invocationTarget == null) ? classMethodSignature.getClassName() : invocationTarget.getClass().getName();
            final String txName = "/Custom/" + className + '/' + classMethodSignature.getMethodName();
            Agent.LOG.log(Level.FINER, "Setting transaction name using instrumented class and method: {0}", new Object[] { txName });
            final Transaction tx = this.transactionActivity.getTransaction();
            tx.setPriorityTransactionName(PriorityTransactionName.create(tx, txName, "Custom", priority));
        }
        catch (Throwable t) {
            Agent.LOG.log(Level.FINEST, "nameTransaction", t);
        }
    }
    
    public TracedMethod getParentTracedMethod() {
        return (TracedMethod)this.getParentTracer();
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    protected Set<String> getRollupMetricNames() {
        return this.rollupMetricNames;
    }
    
    protected Set<String> getExclusiveRollupMetricNames() {
        return this.exclusiveRollupMetricNames;
    }
    
    public void addRollupMetricName(final String... metricNameParts) {
        if (this.rollupMetricNames == null) {
            this.rollupMetricNames = new HashSet<String>();
        }
        this.rollupMetricNames.add(Strings.join('/', metricNameParts));
    }
    
    public void setRollupMetricNames(final String... metricNames) {
        this.rollupMetricNames = new HashSet<String>(metricNames.length);
        for (final String metricName : metricNames) {
            this.rollupMetricNames.add(metricName);
        }
    }
    
    public void addExclusiveRollupMetricName(final String... metricNameParts) {
        if (this.exclusiveRollupMetricNames == null) {
            this.exclusiveRollupMetricNames = new HashSet<String>();
        }
        this.exclusiveRollupMetricNames.add(Strings.join('/', metricNameParts));
    }
}

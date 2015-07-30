// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.AbstractTracerFactory;
import com.newrelic.agent.tracers.IgnoreTransactionTracerFactory;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.deps.com.google.common.collect.ComparisonChain;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.TracerFactory;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassAndMethodMatcher;

public abstract class PointCut implements Comparable<PointCut>, ClassAndMethodMatcher
{
    protected static final int HIGH_PRIORITY = Integer.MAX_VALUE;
    protected static final int DEFAULT_PRIORITY = 20;
    protected static final int LOW_PRIORITY = Integer.MIN_VALUE;
    private final ClassMatcher classMatcher;
    private final MethodMatcher methodMatcher;
    private final PointCutConfiguration config;
    private TracerFactory tracerFactory;
    private int priority;
    private final boolean isIgnoreTransaction;
    
    protected PointCut(final PointCutConfiguration config, final ClassMatcher classMatcher, final MethodMatcher methodMatcher) {
        this.priority = 20;
        assert config != null;
        this.classMatcher = classMatcher;
        this.methodMatcher = methodMatcher;
        this.config = config;
        this.isIgnoreTransaction = config.getConfiguration().getProperty("ignore_transaction", false);
    }
    
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }
    
    public boolean isEnabled() {
        return this.config.isEnabled();
    }
    
    protected void logInstrumentation(final String className, final Class<?> classBeingRedefined) {
        if (Agent.isDebugEnabled()) {
            Agent.LOG.finer(MessageFormat.format("Instrumenting {0} {1}", className, (classBeingRedefined == null) ? "" : "(Second pass)"));
        }
    }
    
    public ClassMatcher getClassMatcher() {
        return this.classMatcher;
    }
    
    protected boolean isDispatcher() {
        return false;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    protected void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public final int compareTo(final PointCut pc) {
        return ComparisonChain.start().compare(pc.getPriority(), this.getPriority()).compare(this.getClass().getName(), pc.getClass().getName()).result();
    }
    
    public void noticeTransformerStarted(final ClassTransformer classTransformer) {
    }
    
    protected abstract PointCutInvocationHandler getPointCutInvocationHandlerImpl();
    
    public final PointCutInvocationHandler getPointCutInvocationHandler() {
        return this.wrapHandler(this.isIgnoreTransaction() ? new IgnoreTransactionTracerFactory() : this.getPointCutInvocationHandlerImpl());
    }
    
    private PointCutInvocationHandler wrapHandler(final PointCutInvocationHandler pointCutInvocationHandler) {
        if (this.isDispatcher() || !(pointCutInvocationHandler instanceof TracerFactory)) {
            return pointCutInvocationHandler;
        }
        if (this.tracerFactory == null) {
            this.tracerFactory = new AbstractTracerFactory() {
                public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
                    if (!PointCut.this.isDispatcher() && !transaction.isStarted()) {
                        return null;
                    }
                    if (transaction.getTransactionActivity().isFlyweight()) {
                        return null;
                    }
                    return ((TracerFactory)pointCutInvocationHandler).getTracer(transaction, sig, object, args);
                }
            };
        }
        return this.tracerFactory;
    }
    
    protected boolean isIgnoreTransaction() {
        return this.isIgnoreTransaction;
    }
    
    public String toString() {
        return (this.config.getName() == null) ? ("PointCut:" + this.getPointCutInvocationHandler().getClass().getName()) : this.config.getName();
    }
    
    protected static MethodMatcher createMethodMatcher(final MethodMatcher... matchers) {
        return OrMethodMatcher.getMethodMatcher(matchers);
    }
    
    protected static MethodMatcher createExactMethodMatcher(final String methodName, final String... methodDescriptions) {
        return new ExactMethodMatcher(methodName, methodDescriptions);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.classMatcher == null) ? 0 : this.classMatcher.hashCode());
        result = 31 * result + ((this.methodMatcher == null) ? 0 : this.methodMatcher.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PointCut other = (PointCut)obj;
        if (this.classMatcher == null) {
            if (other.classMatcher != null) {
                return false;
            }
        }
        else if (!this.classMatcher.equals(other.classMatcher)) {
            return false;
        }
        if (this.methodMatcher == null) {
            if (other.methodMatcher != null) {
                return false;
            }
        }
        else if (!this.methodMatcher.equals(other.methodMatcher)) {
            return false;
        }
        return true;
    }
}

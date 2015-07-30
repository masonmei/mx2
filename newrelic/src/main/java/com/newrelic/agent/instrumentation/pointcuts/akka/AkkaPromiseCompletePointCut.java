// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.akka;

import com.newrelic.agent.instrumentation.pointcuts.scala.Either;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class AkkaPromiseCompletePointCut extends PointCut implements EntryInvocationHandler
{
    public static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    
    public AkkaPromiseCompletePointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(AkkaPromiseCompletePointCut.POINT_CUT_NAME, "akka_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("akka/dispatch/DefaultPromise");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new NameMethodMatcher("tryComplete");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (object instanceof TransactionHolder) {
            final TransactionHolder promise = (TransactionHolder)object;
            final Transaction tx = (Transaction)promise._nr_getTransaction();
            if (tx == null || !tx.isStarted()) {
                return;
            }
            this.finishTracer(promise, args);
            tx.getTransactionState().asyncJobFinished(promise);
        }
    }
    
    private void finishTracer(final TransactionHolder promise, final Object[] args) {
        if (promise instanceof AkkaTracerHolder) {
            final AkkaTracerHolder tracerHolder = (AkkaTracerHolder)promise;
            if (tracerHolder._nr_getTracer() instanceof Tracer) {
                final Tracer tracer = (Tracer)tracerHolder._nr_getTracer();
                if (args[0] instanceof Either) {
                    final Object resolved = ((Either)args[0]).get();
                    if (resolved instanceof Throwable) {
                        tracer.finish((Throwable)resolved);
                    }
                    else {
                        tracer.finish(176, resolved);
                    }
                }
                tracerHolder._nr_setTracer(null);
            }
        }
    }
    
    static {
        POINT_CUT_NAME = AkkaPromiseCompletePointCut.class.getName();
    }
}

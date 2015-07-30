// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.TransactionActivity;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.instrumentation.pointcuts.asynchttp.AsyncHttpClientTracer;
import com.newrelic.agent.instrumentation.pointcuts.asynchttp.AsyncHttpClientRequestPointCut;
import com.newrelic.agent.instrumentation.pointcuts.TransactionHolder;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ScalaAbstractPromiseCompletePointCut extends PointCut implements EntryInvocationHandler
{
    public static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    private static final String DESC = "(Ljava/lang/Object;Ljava/lang/Object;)Z";
    private static final String METHOD = "updateState";
    
    public ScalaAbstractPromiseCompletePointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(ScalaAbstractPromiseCompletePointCut.POINT_CUT_NAME, "scala_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("scala/concurrent/impl/AbstractPromise");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("updateState", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        final boolean replacingTH = args[0] instanceof ScalaTransactionHolder;
        final boolean beingLinked = args[1] instanceof ScalaTransactionHolder;
        final boolean completing = args[1] instanceof ScalaTry;
        Object target = null;
        ScalaTry result = null;
        if (completing) {
            target = object;
            result = (ScalaTry)args[1];
        }
        if (beingLinked && !replacingTH) {
            target = args[1];
        }
        if (target instanceof ScalaTransactionHolder) {
            final ScalaTransactionHolder promise = (ScalaTransactionHolder)object;
            final Transaction tx = (Transaction)promise._nr_getTransaction();
            if (tx == null || !tx.isStarted()) {
                return;
            }
            this.finishTracer(promise, result);
            tx.getTransactionState().asyncJobFinished(promise);
        }
    }
    
    private void finishTracer(final ScalaTransactionHolder promise, final ScalaTry result) {
        if (promise instanceof ScalaTracerHolder) {
            final ScalaTracerHolder tracerHolder = (ScalaTracerHolder)promise;
            if (tracerHolder._nr_getTracer() instanceof AsyncHttpClientRequestPointCut.AsyncHttpClientTracerInfo) {
                final AsyncHttpClientRequestPointCut.AsyncHttpClientTracerInfo tracerInfo = (AsyncHttpClientRequestPointCut.AsyncHttpClientTracerInfo)tracerHolder._nr_getTracer();
                final Transaction savedTx = (Transaction)promise._nr_getTransaction();
                final Transaction tx = Transaction.getTransaction();
                final TransactionActivity txa = tx.getTransactionActivity();
                final String txName = (String)promise._nr_getName();
                final AsyncHttpClientTracer tracer = new AsyncHttpClientTracer(tx, txName, tracerInfo.getClassMethodSignature(), null, tracerInfo.getHost(), "AsyncHttpClient", tracerInfo.getUri(), tracerInfo.getStartTime(), tracerInfo.getMethodName());
                tx.getTransactionActivity().tracerStarted(tracer);
                if (result instanceof ScalaSuccess) {
                    final ScalaSuccess success = (ScalaSuccess)result;
                    tracer.setResponse(success._nr_value());
                    tracer.finish(176, success);
                    if (tx.getRootTracer() == tracer) {
                        savedTx.getTransactionState().asyncTransactionStarted(tx, promise);
                        savedTx.getTransactionState().asyncTransactionFinished(txa);
                    }
                }
                else if (result instanceof ScalaFailure) {
                    tracer.finish(((ScalaFailure)result)._nr_exception());
                }
                tracerHolder._nr_setTracer(null);
            }
        }
    }
    
    static {
        POINT_CUT_NAME = ScalaAbstractPromiseCompletePointCut.class.getName();
    }
}

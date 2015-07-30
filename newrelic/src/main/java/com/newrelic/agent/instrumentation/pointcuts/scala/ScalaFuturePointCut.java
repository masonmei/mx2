// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.scala;

import com.newrelic.agent.tracers.MethodExitTracerNoSkip;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.NameMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ScalaFuturePointCut extends TracerFactoryPointCut
{
    public static final boolean DEFAULT_ENABLED = true;
    private static final String CLASS_NAME = "scala/concurrent/Future$";
    private static final String METHOD_NAME = "firstCompletedOf";
    private static final String POINT_CUT_NAME;
    
    public ScalaFuturePointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(ScalaFuturePointCut.POINT_CUT_NAME, "scala_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("scala/concurrent/Future$");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new NameMethodMatcher("firstCompletedOf");
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (!tx.isStarted()) {
            return null;
        }
        tx.getTransactionState().setInvalidateAsyncJobs(true);
        return new MethodExitTracerNoSkip(sig, tx) {
            protected void doFinish(final int opcode, final Object returnValue) {
                tx.getTransactionState().setInvalidateAsyncJobs(false);
            }
            
            public void finish(final Throwable throwable) {
                tx.getTransactionState().setInvalidateAsyncJobs(false);
            }
        };
    }
    
    static {
        POINT_CUT_NAME = ScalaFuturePointCut.class.getName();
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play;

import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class PlayContinuationPointCut extends TracerFactoryPointCut
{
    private static final String POINT_CUT_NAME;
    private static final String CONTINUATION_CLASS = "com/newrelic/agent/deps/org/apache/commons/javaflow/Continuation";
    private static final String SUSPEND_METHOD_NAME = "suspend";
    private static final String SUSPEND_METHOD_DESC = "(Ljava/lang/Object;)Ljava/lang/Object;";
    
    public PlayContinuationPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(PlayContinuationPointCut.POINT_CUT_NAME, "play_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("com/newrelic/agent/deps/org/apache/commons/javaflow/Continuation");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("suspend", "(Ljava/lang/Object;)Ljava/lang/Object;");
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        tx.getTransactionState().suspend();
        return null;
    }
    
    static {
        POINT_CUT_NAME = PlayContinuationPointCut.class.getName();
    }
}

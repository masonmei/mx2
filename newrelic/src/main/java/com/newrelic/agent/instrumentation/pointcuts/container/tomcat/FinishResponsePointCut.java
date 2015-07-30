// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.tomcat;

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
public class FinishResponsePointCut extends PointCut implements EntryInvocationHandler
{
    private static final String POINT_CUT_NAME;
    private static final boolean DEFAULT_ENABLED = true;
    private static final String COYOTE_RESPONSE_CLASS = "org/apache/catalina/connector/Response";
    private static final String FINISH_RESPONSE_METHOD_NAME = "finishResponse";
    private static final String FINISH_RESPONSE_METHOD_DESC = "()V";
    
    public FinishResponsePointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(FinishResponsePointCut.POINT_CUT_NAME, "tomcat", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("org/apache/catalina/connector/Response");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("finishResponse", "()V");
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        final Transaction tx = Transaction.getTransaction();
        if (tx == null) {
            return;
        }
        tx.beforeSendResponseHeaders();
    }
    
    static {
        POINT_CUT_NAME = FinishResponsePointCut.class.getName();
    }
}

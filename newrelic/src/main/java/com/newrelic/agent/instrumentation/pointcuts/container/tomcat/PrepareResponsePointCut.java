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
public class PrepareResponsePointCut extends PointCut implements EntryInvocationHandler
{
    private static final String POINT_CUT_NAME;
    private static final boolean DEFAULT_ENABLED = true;
    private static final String COYOTE_ABSTRACT_HTTP11_PROCESSOR_CLASS = "org/apache/coyote/http11/AbstractHttp11Processor";
    private static final String COYOTE_HTTP11_PROCESSOR_CLASS = "org/apache/coyote/http11/Http11Processor";
    private static final String GRIZZLY_PROCESSOR_TASK_CLASS = "com/sun/grizzly/http/ProcessorTask";
    private static final String PREPARE_RESPONSE_METHOD_NAME = "prepareResponse";
    private static final String PREPARE_RESPONSE_METHOD_DESC = "()V";
    
    public PrepareResponsePointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(PrepareResponsePointCut.POINT_CUT_NAME, "tomcat", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return ExactClassMatcher.or("org/apache/coyote/http11/AbstractHttp11Processor", "org/apache/coyote/http11/Http11Processor", "com/sun/grizzly/http/ProcessorTask");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("prepareResponse", "()V");
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
        POINT_CUT_NAME = PrepareResponsePointCut.class.getName();
    }
}

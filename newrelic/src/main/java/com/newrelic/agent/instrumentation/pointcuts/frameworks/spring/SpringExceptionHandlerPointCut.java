// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.spring;

import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.errors.AbstractExceptionHandlerPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SpringExceptionHandlerPointCut extends AbstractExceptionHandlerPointCut
{
    private static final String PROCESS_HANDLER_EXCEPTION_METHOD_NAME = "processHandlerException";
    
    public SpringExceptionHandlerPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration("spring_exception_handler", "spring_framework", true),
                new ExactClassMatcher("org/springframework/web/servlet/DispatcherServlet"),
                PointCut.createMethodMatcher(new ExactMethodMatcher("processHandlerException", "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/Object;Ljava/lang/Exception;)Lorg/springframework/web/servlet/ModelAndView;"), new ExactMethodMatcher("triggerAfterCompletion", "(Lorg/springframework/web/servlet/HandlerExecutionChain;ILjavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/Exception;)V")));
    }
    
    protected Throwable getThrowable(final ClassMethodSignature sig, final Object[] args) {
        final int index = "processHandlerException".equals(sig.getMethodName()) ? 3 : 4;
        return (Throwable)args[index];
    }
}

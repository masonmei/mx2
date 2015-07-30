// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play2;

import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.OtherRootTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class CreateInvokerPointCut extends TracerFactoryPointCut
{
    private static final boolean DEFAULT_ENABLED = true;
    private static final String POINT_CUT_NAME;
    static final String CLASS = "play/core/Router$Routes";
    static final String METHOD_NAME = "createInvoker";
    static final String METHOD_DESC = "(Lscala/Function0;Lplay/core/Router$HandlerDef;Lplay/core/Router$HandlerInvokerFactory;)Lplay/core/Router$HandlerInvoker;";
    
    public CreateInvokerPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(CreateInvokerPointCut.POINT_CUT_NAME, "play2_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new InterfaceMatcher("play/core/Router$Routes");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("createInvoker", "(Lscala/Function0;Lplay/core/Router$HandlerDef;Lplay/core/Router$HandlerInvokerFactory;)Lplay/core/Router$HandlerInvoker;");
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object target, final Object[] args) {
        return new CreateInvokerTracer(transaction, sig, target, args);
    }
    
    static {
        POINT_CUT_NAME = CreateInvokerPointCut.class.getName();
    }
    
    private static class CreateInvokerTracer extends OtherRootTracer
    {
        private final HandlerDef handlerDef;
        
        public CreateInvokerTracer(final Transaction transaction, final ClassMethodSignature sig, final Object target, final Object[] args) {
            super(transaction, sig, target, new ClassMethodMetricNameFormat(sig, target, "OtherTransaction/Job"));
            this.handlerDef = ((args[1] instanceof HandlerDef) ? ((HandlerDef)args[1]) : null);
        }
        
        protected void doFinish(final int opcode, final Object returnValue) {
            if (this.handlerDef != null && returnValue instanceof TaggingInvoker) {
                final TaggingInvoker taggingInvoker = (TaggingInvoker)returnValue;
                Agent.LOG.log(Level.FINEST, "TaggingInvoker.setHandlerDef {0}.{1}", new Object[] { this.handlerDef.controller(), this.handlerDef.method() });
                taggingInvoker.setHandlerDef(this.handlerDef);
            }
        }
    }
}

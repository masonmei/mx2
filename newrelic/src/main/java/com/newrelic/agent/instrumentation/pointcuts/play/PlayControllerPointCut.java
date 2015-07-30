// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.play;

import java.text.MessageFormat;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.OrMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class PlayControllerPointCut extends TracerFactoryPointCut
{
    private static final String POINT_CUT_NAME;
    private static final String CONTROLLER_CLASS = "play/mvc/Controller";
    private static final String AWAIT_METHOD_NAME = "await";
    private static final String AWAIT_METHOD_DESC_1 = "(I)V";
    private static final String AWAIT_METHOD_DESC_2 = "(Ljava/util/concurrent/Future;)Ljava/lang/Object;";
    private static final String AWAIT_METHOD_DESC_3 = "(ILplay/libs/F$Action0;)V";
    private static final String AWAIT_METHOD_DESC_4 = "(Ljava/util/concurrent/Future;Lplay/libs/F$Action;)V";
    private static final String RENDER_TEMPLATE_METHOD_NAME = "renderTemplate";
    private static final String RENDER_TEMPLATE_METHOD_DESC = "(Ljava/lang/String;Ljava/util/Map;)V";
    private static final String TEMPLATE_METRIC_NAME = "Controller.renderTemplate/{0}";
    private static final String AWAIT_METRIC_NAME = "Controller.await";
    private static final String SUSPEND_EXECPTION_CLASS = "play.Invoker$Suspend";
    
    public PlayControllerPointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(PlayControllerPointCut.POINT_CUT_NAME, "play_instrumentation", true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("play/mvc/Controller");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return OrMethodMatcher.getMethodMatcher(new ExactMethodMatcher("renderTemplate", "(Ljava/lang/String;Ljava/util/Map;)V"), new ExactMethodMatcher("await", new String[] { "(I)V", "(Ljava/util/concurrent/Future;)Ljava/lang/Object;", "(ILplay/libs/F$Action0;)V", "(Ljava/util/concurrent/Future;Lplay/libs/F$Action;)V" }));
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        if ("await" == sig.getMethodName()) {
            return this.getAwaitTracer(tx, sig, object, args);
        }
        return this.getRenderTracer(tx, sig, object, args);
    }
    
    private Tracer getAwaitTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        final MetricNameFormat format = new SimpleMetricNameFormat("Controller.await");
        if (sig.getMethodDesc() == "(ILplay/libs/F$Action0;)V" || sig.getMethodDesc() == "(Ljava/util/concurrent/Future;Lplay/libs/F$Action;)V") {
            return new PlayControllerTracer(tx, sig, object, format);
        }
        return new DefaultTracer(tx, sig, object, format);
    }
    
    private Tracer getRenderTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final Object[] args) {
        final String templateName = (String)args[0];
        final String metricName = MessageFormat.format("Controller.renderTemplate/{0}", templateName);
        final MetricNameFormat format = new SimpleMetricNameFormat(metricName);
        return new DefaultTracer(tx, sig, object, format);
    }
    
    static {
        POINT_CUT_NAME = PlayControllerPointCut.class.getName();
    }
    
    private static class PlayControllerTracer extends DefaultTracer
    {
        public PlayControllerTracer(final Transaction tx, final ClassMethodSignature sig, final Object object, final MetricNameFormat format) {
            super(tx, sig, object, format);
        }
        
        protected void doFinish(final Throwable throwable) {
            if (throwable.getClass().getName() == "play.Invoker$Suspend") {
                this.getTransaction().getTransactionState().suspendRootTracer();
            }
        }
    }
}

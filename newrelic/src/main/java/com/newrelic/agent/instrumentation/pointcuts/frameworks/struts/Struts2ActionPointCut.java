// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.struts;

import java.text.MessageFormat;
import com.newrelic.agent.bridge.TransactionNamePriority;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.tracers.metricname.ClassMethodMetricNameFormat;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.InterfaceMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class Struts2ActionPointCut extends TracerFactoryPointCut
{
    public static final String STRUTS_ACTION__PROXY_INTERFACE = "com/opensymphony/xwork2/ActionProxy";
    private static final MethodMatcher METHOD_MATCHER;
    
    public Struts2ActionPointCut(final ClassTransformer classTransformer) {
        super(Struts2ActionPointCut.class, new InterfaceMatcher("com/opensymphony/xwork2/ActionProxy"), Struts2ActionPointCut.METHOD_MATCHER);
    }
    
    public Tracer doGetTracer(final Transaction tx, final ClassMethodSignature sig, final Object action, final Object[] args) {
        try {
            String realAction;
            if (action instanceof ActionProxy) {
                realAction = ((ActionProxy)action).getActionName();
            }
            else {
                realAction = (String)action.getClass().getMethod("getActionName", (Class<?>[])new Class[0]).invoke(action, new Object[0]);
            }
            this.setTransactionName(tx, realAction);
            return new DefaultTracer(tx, sig, action, new SimpleMetricNameFormat("StrutsAction/" + realAction));
        }
        catch (Exception e) {
            return new DefaultTracer(tx, sig, action, new ClassMethodMetricNameFormat(sig, action, "StrutsAction"));
        }
    }
    
    private void setTransactionName(final Transaction tx, final String action) {
        if (!tx.isTransactionNamingEnabled()) {
            return;
        }
        final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
        if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(tx, TransactionNamePriority.FRAMEWORK)) {
            final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Struts 2 action", action);
            Agent.LOG.finer(msg);
        }
        policy.setTransactionName(tx, action, "StrutsAction", TransactionNamePriority.FRAMEWORK);
    }
    
    static {
        METHOD_MATCHER = PointCut.createExactMethodMatcher("execute", "()Ljava/lang/String;");
    }
}

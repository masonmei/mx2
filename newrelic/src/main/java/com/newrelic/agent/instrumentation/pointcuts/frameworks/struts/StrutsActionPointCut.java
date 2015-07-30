// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.struts;

import java.text.MessageFormat;
import com.newrelic.agent.bridge.TransactionNamePriority;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.instrumentation.PointCut;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.tracers.metricname.MetricNameFormat;
import com.newrelic.agent.tracers.metricname.SimpleMetricNameFormat;
import com.newrelic.agent.tracers.DefaultTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ChildClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class StrutsActionPointCut extends TracerFactoryPointCut
{
    public static final String STRUTS_ACTION_CLASS = "org/apache/struts/action/Action";
    private static final MethodMatcher METHOD_MATCHER;
    
    public StrutsActionPointCut(final ClassTransformer classTransformer) {
        super(StrutsActionPointCut.class, new ChildClassMatcher("org/apache/struts/action/Action"), StrutsActionPointCut.METHOD_MATCHER);
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object action, final Object[] args) {
        return new StrutsActionTracer(transaction, sig, action, args);
    }
    
    static {
        METHOD_MATCHER = PointCut.createExactMethodMatcher("execute", "(Lorg/apache/struts/action/ActionMapping;Lorg/apache/struts/action/ActionForm;Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)Lorg/apache/struts/action/ActionForward;", "(Lorg/apache/struts/action/ActionMapping;Lorg/apache/struts/action/ActionForm;Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)Lorg/apache/struts/action/ActionForward;");
    }
    
    private static class StrutsActionTracer extends DefaultTracer
    {
        private final String actionClassName;
        
        public StrutsActionTracer(final Transaction transaction, final ClassMethodSignature sig, final Object action, final Object[] args) {
            super(transaction, sig, action);
            this.setTransactionName(transaction, this.actionClassName = action.getClass().getName());
            this.setMetricNameFormat(new SimpleMetricNameFormat("StrutsAction/" + this.actionClassName));
        }
        
        private void setTransactionName(final Transaction tx, final String action) {
            if (!tx.isTransactionNamingEnabled()) {
                return;
            }
            final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
            if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(tx, TransactionNamePriority.FRAMEWORK)) {
                final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Struts action", action);
                Agent.LOG.finer(msg);
            }
            policy.setTransactionName(tx, action, "StrutsAction", TransactionNamePriority.FRAMEWORK);
        }
    }
}

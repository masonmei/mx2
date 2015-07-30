// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks.struts;

import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.newrelic.agent.Agent;
import com.newrelic.agent.util.Invoker;
import com.newrelic.agent.tracers.MethodExitTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class StrutsActionConfigMatcherPointCut extends TracerFactoryPointCut
{
    private static final String STRUTS = "Struts";
    private static final String ACTION_CONFIG_MATCHER_CLASS = "org/apache/struts/config/ActionConfigMatcher";
    private static final String GET_PATH = "getPath";
    
    public StrutsActionConfigMatcherPointCut(final ClassTransformer classTransformer) {
        super(StrutsActionConfigMatcherPointCut.class, new ExactClassMatcher("org/apache/struts/config/ActionConfigMatcher"), PointCut.createExactMethodMatcher("convertActionConfig", "(Ljava/lang/String;Lorg/apache/struts/config/ActionConfig;Ljava/util/Map;)Lorg/apache/struts/config/ActionConfig;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object matcher, final Object[] args) {
        return new StrutsActionConfigMatcherTracer(transaction, sig, matcher, args);
    }
    
    private static class StrutsActionConfigMatcherTracer extends MethodExitTracer
    {
        public StrutsActionConfigMatcherTracer(final Transaction transaction, final ClassMethodSignature sig, final Object matcher, final Object[] args) {
            super(sig, transaction);
            try {
                final Object actionConfig = args[1];
                final String wildcardPath = (String)Invoker.invoke(actionConfig, actionConfig.getClass(), "getPath", new Object[0]);
                Agent.LOG.finer("Normalizing path using Struts wildcard");
                this.setTransactionName(transaction, wildcardPath);
            }
            catch (Exception e) {
                final String msg = MessageFormat.format("Exception in {0} handling {1}: {2}", StrutsActionConfigMatcherPointCut.class.getSimpleName(), sig, e);
                if (Agent.LOG.isLoggable(Level.FINEST)) {
                    Agent.LOG.log(Level.FINEST, msg, e);
                }
                else {
                    Agent.LOG.finer(msg);
                }
            }
        }
        
        private void setTransactionName(final Transaction transaction, final String wildcardPath) {
            if (!transaction.isTransactionNamingEnabled()) {
                return;
            }
            final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
            if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(transaction, TransactionNamePriority.FRAMEWORK)) {
                final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using Stuts wildcard", wildcardPath);
                Agent.LOG.finer(msg);
            }
            policy.setTransactionName(transaction, wildcardPath, "Struts", TransactionNamePriority.FRAMEWORK);
        }
        
        protected void doFinish(final int opcode, final Object returnValue) {
        }
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.frameworks;

import java.text.MessageFormat;
import com.newrelic.agent.bridge.TransactionNamePriority;
import java.util.logging.Level;
import com.newrelic.agent.transaction.TransactionNamingPolicy;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.MethodExitTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class PathMapperPointCut extends TracerFactoryPointCut
{
    private static final String SITEMESH = "SiteMesh";
    
    public PathMapperPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(PathMapperPointCut.class.getName(), null, false), new ExactClassMatcher("com/opensymphony/module/sitemesh/mapper/PathMapper"), new ExactMethodMatcher("findKey", "(Ljava/lang/String;Ljava/util/Map;)Ljava/lang/String;"));
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        return new PathMapperTracer(transaction, sig, object, (String)args[0]);
    }
    
    private static class PathMapperTracer extends MethodExitTracer
    {
        public PathMapperTracer(final Transaction transaction, final ClassMethodSignature sig, final Object mapper, final String path) {
            super(sig, transaction);
        }
        
        protected void doFinish(final int opcode, final Object key) {
            if (key != null) {
                Agent.LOG.finer("Normalizing path using SiteMesh config");
                String path = key.toString();
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                this.setTransactionName(this.getTransaction(), path);
            }
        }
        
        private void setTransactionName(final Transaction transaction, final String path) {
            if (!transaction.isTransactionNamingEnabled()) {
                return;
            }
            final TransactionNamingPolicy policy = TransactionNamingPolicy.getHigherPriorityTransactionNamingPolicy();
            if (Agent.LOG.isLoggable(Level.FINER) && policy.canSetTransactionName(transaction, TransactionNamePriority.FRAMEWORK)) {
                final String msg = MessageFormat.format("Setting transaction name to \"{0}\" using SiteMesh config", path);
                Agent.LOG.finer(msg);
            }
            policy.setTransactionName(transaction, path, "SiteMesh", TransactionNamePriority.FRAMEWORK);
        }
    }
}

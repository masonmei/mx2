// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.glassfish;

import java.text.MessageFormat;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import com.newrelic.agent.jmx.values.GlassfishJmxValues;
import com.newrelic.agent.service.ServiceFactory;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.tracers.MethodExitTracer;
import com.newrelic.agent.tracers.Tracer;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.Transaction;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import java.util.concurrent.atomic.AtomicBoolean;
import com.newrelic.agent.instrumentation.pointcuts.PointCut;
import com.newrelic.agent.instrumentation.TracerFactoryPointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class Glassfish4StartUpPointCut extends TracerFactoryPointCut
{
    private final AtomicBoolean addedJmx;
    
    public Glassfish4StartUpPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(Glassfish4StartUpPointCut.class.getName(), "glassfish_instrumentation", true), createClassMatcher(), createMethodMatcher());
        this.addedJmx = new AtomicBoolean(false);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("com/sun/appserv/server/util/Version");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return PointCut.createExactMethodMatcher("getMajorVersion", "()Ljava/lang/String;");
    }
    
    protected boolean isDispatcher() {
        return true;
    }
    
    public Tracer doGetTracer(final Transaction transaction, final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (!this.addedJmx.get()) {
            return new MethodExitTracer(sig, transaction) {
                protected void doFinish(final int opcode, final Object returnValue) {
                    try {
                        if (returnValue instanceof String) {
                            final String majorVersion = ((String)returnValue).trim();
                            if (majorVersion.length() > 0) {
                                Glassfish4StartUpPointCut.this.addJMX(majorVersion);
                            }
                        }
                    }
                    catch (Exception e) {
                        if (Agent.LOG.isFinestEnabled()) {
                            Agent.LOG.log(Level.FINER, "Glassfish Jmx error", e);
                        }
                    }
                }
            };
        }
        return null;
    }
    
    private void addJMX(final String majorVersion) {
        if (!"1".equals(majorVersion) && !"2".equals(majorVersion) && !"3".equals(majorVersion)) {
            ServiceFactory.getJmxService().addJmxFrameworkValues(new GlassfishJmxValues());
            this.addedJmx.set(true);
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, MessageFormat.format("Added JMX for Glassfish {0}", majorVersion));
            }
        }
    }
}

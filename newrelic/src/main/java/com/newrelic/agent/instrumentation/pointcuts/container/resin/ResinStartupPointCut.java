// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.resin;

import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import com.newrelic.agent.jmx.values.ResinJmxValues;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class ResinStartupPointCut extends PointCut implements EntryInvocationHandler
{
    public static final String RESIN_INSTRUMENTATION_GROUP_NAME = "resin_instrumentation";
    private boolean addJmx;
    
    public ResinStartupPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(ResinStartupPointCut.class.getName(), "resin_instrumentation", true), new ExactClassMatcher("com.caucho.server.resin/Resin"), PointCut.createExactMethodMatcher("start", "()V"));
        this.addJmx = false;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (!this.addJmx) {
            ServiceFactory.getJmxService().addJmxFrameworkValues(new ResinJmxValues());
            this.addJmx = true;
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Added JMX for Resin");
            }
        }
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
}

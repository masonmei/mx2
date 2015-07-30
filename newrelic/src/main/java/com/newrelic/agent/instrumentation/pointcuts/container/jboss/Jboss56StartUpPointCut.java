// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.jboss;

import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import com.newrelic.agent.jmx.values.Jboss56JmxValues;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class Jboss56StartUpPointCut extends PointCut implements EntryInvocationHandler
{
    private boolean addedJmx;
    
    public Jboss56StartUpPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(Jboss56StartUpPointCut.class.getName(), "jboss_instrumentation", true), createClassMatcher(), PointCut.createExactMethodMatcher("boot", "([Ljava/lang/String;)V"));
        this.addedJmx = false;
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("org/jboss/Main");
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (!this.addedJmx) {
            ServiceFactory.getJmxService().addJmxFrameworkValues(new Jboss56JmxValues());
            this.addedJmx = true;
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Added JMX for Jboss");
            }
        }
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
}

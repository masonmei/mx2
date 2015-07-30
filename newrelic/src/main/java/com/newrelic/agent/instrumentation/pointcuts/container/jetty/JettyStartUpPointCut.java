// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.jetty;

import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import com.newrelic.agent.jmx.values.JettyJmxMetrics;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class JettyStartUpPointCut extends PointCut implements EntryInvocationHandler
{
    private boolean addedJmx;
    
    public JettyStartUpPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(JettyStartUpPointCut.class.getName(), null, true), createClassMatcher(), PointCut.createExactMethodMatcher("start", "(Ljava/util/List;)V"));
        this.addedJmx = false;
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("org/eclipse/jetty/start/Main");
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (!this.addedJmx) {
            ServiceFactory.getJmxService().addJmxFrameworkValues(new JettyJmxMetrics());
            this.addedJmx = true;
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Added JMX for Jetty");
            }
        }
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
}

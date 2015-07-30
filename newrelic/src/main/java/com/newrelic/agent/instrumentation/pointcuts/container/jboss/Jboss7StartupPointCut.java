// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.jboss;

import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import com.newrelic.agent.jmx.values.Jboss7UpJmxValues;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class Jboss7StartupPointCut extends PointCut implements EntryInvocationHandler
{
    public static final String JBOSS_INSTRUMENTATION_GROUP_NAME = "jboss_instrumentation";
    private boolean addedJmx;
    
    public Jboss7StartupPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(Jboss7StartupPointCut.class.getName(), "jboss_instrumentation", true), createClassMatcher(), new ExactMethodMatcher("installMBeanServer", "()V"));
        this.addedJmx = false;
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("org/jboss/modules/ModuleLoader");
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (!this.addedJmx) {
            ServiceFactory.getJmxService().addJmxFrameworkValues(new Jboss7UpJmxValues());
            this.addedJmx = true;
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Added JMX for Jboss/Wildfly");
            }
        }
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.container.glassfish;

import com.newrelic.agent.tracers.PointCutInvocationHandler;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import com.newrelic.agent.jmx.values.GlassfishJmxValues;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class Glassfish3StartUpPointCut extends PointCut implements EntryInvocationHandler
{
    public static final String GLASSFISH_INSTRUMENTATION_GROUP_NAME = "glassfish_instrumentation";
    private boolean addedJmx;
    
    public Glassfish3StartUpPointCut(final ClassTransformer classTransformer) {
        super(new PointCutConfiguration(Glassfish3StartUpPointCut.class.getName(), "glassfish_instrumentation", true), createClassMatcher(), createMethodMatcher());
        this.addedJmx = false;
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("com/sun/enterprise/v3/server/SystemTasks");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return PointCut.createExactMethodMatcher("postConstruct", "()V");
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object object, final Object[] args) {
        if (!this.addedJmx) {
            ServiceFactory.getJmxService().addJmxFrameworkValues(new GlassfishJmxValues());
            this.addedJmx = true;
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Added JMX for Glassfish");
            }
        }
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.pointcuts.solr;

import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.jmx.metrics.JmxFrameworkValues;
import com.newrelic.agent.jmx.values.SolrJmxValues;
import java.io.InputStream;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Agent;
import java.util.jar.Manifest;
import com.newrelic.agent.tracers.ClassMethodSignature;
import com.newrelic.agent.tracers.PointCutInvocationHandler;
import com.newrelic.agent.instrumentation.methodmatchers.ExactMethodMatcher;
import com.newrelic.agent.instrumentation.methodmatchers.MethodMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ExactClassMatcher;
import com.newrelic.agent.instrumentation.classmatchers.ClassMatcher;
import com.newrelic.agent.instrumentation.PointCutConfiguration;
import com.newrelic.agent.instrumentation.ClassTransformer;
import java.util.concurrent.atomic.AtomicBoolean;
import com.newrelic.agent.tracers.EntryInvocationHandler;
import com.newrelic.agent.instrumentation.PointCut;

@com.newrelic.agent.instrumentation.pointcuts.PointCut
public class SolrCorePointCut extends PointCut implements EntryInvocationHandler
{
    private static final String POINT_CUT_NAME;
    private static final boolean DEFAULT_ENABLED = true;
    private static final String SOLR_CORE_CLASS = "org/apache/solr/core/SolrCore";
    private static final String INIT_INDEX_METHOD_NAME = "initIndex";
    private static final String INIT_INDEX_METHOD_DESC = "()V";
    private static final String INIT_INDEX_METHOD_4_0_DESC = "(Z)V";
    private final AtomicBoolean addJmx;
    
    public SolrCorePointCut(final ClassTransformer classTransformer) {
        super(createPointCutConfig(), createClassMatcher(), createMethodMatcher());
        this.addJmx = new AtomicBoolean(false);
    }
    
    private static PointCutConfiguration createPointCutConfig() {
        return new PointCutConfiguration(SolrCorePointCut.POINT_CUT_NAME, null, true);
    }
    
    private static ClassMatcher createClassMatcher() {
        return new ExactClassMatcher("org/apache/solr/core/SolrCore");
    }
    
    private static MethodMatcher createMethodMatcher() {
        return new ExactMethodMatcher("initIndex", new String[] { "()V", "(Z)V" });
    }
    
    protected PointCutInvocationHandler getPointCutInvocationHandlerImpl() {
        return this;
    }
    
    public void handleInvocation(final ClassMethodSignature sig, final Object core, final Object[] args) {
        Object version = null;
        try {
            final InputStream iStream = core.getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
            try {
                final Manifest manifest = new Manifest(iStream);
                version = manifest.getMainAttributes().getValue("Specification-Version");
                iStream.close();
            }
            finally {
                iStream.close();
            }
        }
        catch (Exception e) {
            Agent.LOG.fine("Unable to determine the Solr version : " + e.toString());
        }
        try {
            version = ((version == null) ? this.getVersion(core) : version);
        }
        catch (Exception e) {
            version = "1.0";
        }
        ServiceFactory.getEnvironmentService().getEnvironment().addSolrVersion(version);
        this.addJmxConfig();
    }
    
    private void addJmxConfig() {
        try {
            if (!this.addJmx.getAndSet(true)) {
                ServiceFactory.getJmxService().addJmxFrameworkValues(new SolrJmxValues());
                if (Agent.LOG.isFinerEnabled()) {
                    Agent.LOG.log(Level.FINER, "Added JMX for Solr");
                }
            }
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unable to add Solr JMX metrics: {0}", e.toString());
            Agent.LOG.severe(msg);
        }
    }
    
    private Object getVersion(final Object core) throws Exception {
        return core.getClass().getMethod("getVersion", (Class<?>[])new Class[0]).invoke(core, new Object[0]);
    }
    
    static {
        POINT_CUT_NAME = SolrCorePointCut.class.getName();
    }
}

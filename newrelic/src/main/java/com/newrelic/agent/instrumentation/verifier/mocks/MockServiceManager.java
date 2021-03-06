// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.verifier.mocks;

import com.newrelic.agent.utilization.UtilizationService;
import com.newrelic.agent.service.async.AsyncTransactionService;
import com.newrelic.agent.browser.BrowserService;
import com.newrelic.agent.circuitbreaker.CircuitBreakerService;
import com.newrelic.agent.service.analytics.InsightsService;
import com.newrelic.agent.reinstrument.RemoteInstrumentationService;
import com.newrelic.agent.attributes.AttributesService;
import com.newrelic.agent.xray.IXRaySessionService;
import com.newrelic.agent.normalization.NormalizationService;
import com.newrelic.agent.environment.EnvironmentService;
import com.newrelic.agent.rpm.RPMConnectionService;
import com.newrelic.agent.samplers.SamplerService;
import com.newrelic.agent.RPMServiceManager;
import com.newrelic.agent.commands.CommandParser;
import com.newrelic.agent.service.analytics.TransactionEventsService;
import com.newrelic.agent.jmx.JmxService;
import com.newrelic.agent.TransactionService;
import com.newrelic.agent.database.DatabaseService;
import com.newrelic.agent.cache.CacheService;
import com.newrelic.agent.sql.SqlTraceService;
import com.newrelic.agent.HarvestService;
import com.newrelic.agent.trace.TransactionTraceService;
import com.newrelic.agent.TracerService;
import com.newrelic.agent.profile.ProfilerService;
import com.newrelic.agent.service.Service;
import java.util.Map;
import com.newrelic.agent.logging.IAgentLogger;
import com.newrelic.agent.ThreadService;
import com.newrelic.agent.service.module.JarCollectorService;
import com.newrelic.agent.IAgent;
import com.newrelic.agent.instrumentation.ClassTransformerService;
import com.newrelic.agent.extension.ExtensionService;
import com.newrelic.agent.stats.StatsService;
import com.newrelic.agent.config.ConfigService;
import com.newrelic.agent.service.ServiceManager;

public class MockServiceManager implements ServiceManager
{
    private ConfigService configService;
    private StatsService statsService;
    private ExtensionService extensionService;
    private ClassTransformerService classTransformerService;
    private IAgent agent;
    private JarCollectorService jarCollectorService;
    private ThreadService threadService;
    
    public MockServiceManager() {
        this.configService = null;
        this.statsService = null;
        this.extensionService = null;
        this.classTransformerService = null;
        this.agent = null;
        this.jarCollectorService = null;
        this.threadService = null;
    }
    
    public void setExtensionService(final ExtensionService extensionService) {
        this.extensionService = extensionService;
    }
    
    public void setClassTransformerService(final ClassTransformerService classTransformerService) {
        this.classTransformerService = classTransformerService;
    }
    
    public void setJarCollectorService(final JarCollectorService jarCollectorService) {
        this.jarCollectorService = jarCollectorService;
    }
    
    public void setAgent(final IAgent agent) {
        this.agent = agent;
    }
    
    public void setConfigService(final ConfigService configService) {
        this.configService = configService;
    }
    
    public void setStatsService(final StatsService statsService) {
        this.statsService = statsService;
    }
    
    public void setThreadService(final ThreadService threadService) {
        this.threadService = threadService;
    }
    
    public String getName() {
        return null;
    }
    
    public void start() throws Exception {
    }
    
    public void stop() throws Exception {
    }
    
    public boolean isEnabled() {
        return false;
    }
    
    public IAgentLogger getLogger() {
        return null;
    }
    
    public boolean isStarted() {
        return false;
    }
    
    public boolean isStopped() {
        return false;
    }
    
    public boolean isStartedOrStarting() {
        return false;
    }
    
    public boolean isStoppedOrStopping() {
        return false;
    }
    
    public Map<String, Map<String, Object>> getServicesConfiguration() {
        return null;
    }
    
    public void addService(final Service service) {
    }
    
    public Service getService(final String name) {
        return null;
    }
    
    public ExtensionService getExtensionService() {
        return this.extensionService;
    }
    
    public ProfilerService getProfilerService() {
        return null;
    }
    
    public TracerService getTracerService() {
        return null;
    }
    
    public TransactionTraceService getTransactionTraceService() {
        return null;
    }
    
    public ThreadService getThreadService() {
        return this.threadService;
    }
    
    public HarvestService getHarvestService() {
        return null;
    }
    
    public SqlTraceService getSqlTraceService() {
        return null;
    }
    
    public CacheService getCacheService() {
        return null;
    }
    
    public DatabaseService getDatabaseService() {
        return null;
    }
    
    public TransactionService getTransactionService() {
        return null;
    }
    
    public JarCollectorService getJarCollectorService() {
        return this.jarCollectorService;
    }
    
    public JmxService getJmxService() {
        return null;
    }
    
    public TransactionEventsService getTransactionEventsService() {
        return null;
    }
    
    public CommandParser getCommandParser() {
        return null;
    }
    
    public RPMServiceManager getRPMServiceManager() {
        return null;
    }
    
    public SamplerService getSamplerService() {
        return null;
    }
    
    public IAgent getAgent() {
        return this.agent;
    }
    
    public ConfigService getConfigService() {
        return this.configService;
    }
    
    public RPMConnectionService getRPMConnectionService() {
        return null;
    }
    
    public EnvironmentService getEnvironmentService() {
        return null;
    }
    
    public ClassTransformerService getClassTransformerService() {
        return this.classTransformerService;
    }
    
    public StatsService getStatsService() {
        return this.statsService;
    }
    
    public NormalizationService getNormalizationService() {
        return null;
    }
    
    public IXRaySessionService getXRaySessionService() {
        return null;
    }
    
    public AttributesService getAttributesService() {
        return null;
    }
    
    public RemoteInstrumentationService getRemoteInstrumentationService() {
        return null;
    }
    
    public InsightsService getInsights() {
        return null;
    }
    
    public CircuitBreakerService getCircuitBreakerService() {
        return null;
    }
    
    public BrowserService getBrowserService() {
        return null;
    }
    
    public AsyncTransactionService getAsyncTxService() {
        return null;
    }
    
    public UtilizationService getUtilizationService() {
        return null;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.service;

import com.newrelic.agent.utilization.UtilizationService;
import com.newrelic.agent.service.async.AsyncTransactionService;
import java.util.Map;
import com.newrelic.agent.attributes.AttributesService;
import com.newrelic.agent.xray.IXRaySessionService;
import com.newrelic.agent.service.module.JarCollectorService;
import com.newrelic.agent.extension.ExtensionService;
import com.newrelic.agent.profile.ProfilerService;
import com.newrelic.agent.TracerService;
import com.newrelic.agent.trace.TransactionTraceService;
import com.newrelic.agent.ThreadService;
import com.newrelic.agent.reinstrument.RemoteInstrumentationService;
import com.newrelic.agent.HarvestService;
import com.newrelic.agent.TransactionService;
import com.newrelic.agent.database.DatabaseService;
import com.newrelic.agent.normalization.NormalizationService;
import com.newrelic.agent.cache.CacheService;
import com.newrelic.agent.browser.BrowserService;
import com.newrelic.agent.sql.SqlTraceService;
import com.newrelic.agent.service.analytics.TransactionEventsService;
import com.newrelic.agent.jmx.JmxService;
import com.newrelic.agent.commands.CommandParser;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.RPMServiceManager;
import com.newrelic.agent.samplers.SamplerService;
import com.newrelic.agent.IAgent;
import com.newrelic.agent.rpm.RPMConnectionService;
import com.newrelic.agent.config.ConfigService;
import com.newrelic.agent.environment.EnvironmentService;
import com.newrelic.agent.instrumentation.ClassTransformerService;
import com.newrelic.agent.stats.StatsService;

public class ServiceFactory
{
    private static volatile ServiceManager SERVICE_MANAGER;
    
    public static void setServiceManager(final ServiceManager serviceManager) {
        if (serviceManager != null) {
            ServiceFactory.SERVICE_MANAGER = serviceManager;
        }
    }
    
    public static ServiceManager getServiceManager() {
        return ServiceFactory.SERVICE_MANAGER;
    }
    
    public static StatsService getStatsService() {
        return ServiceFactory.SERVICE_MANAGER.getStatsService();
    }
    
    public static ClassTransformerService getClassTransformerService() {
        return ServiceFactory.SERVICE_MANAGER.getClassTransformerService();
    }
    
    public static EnvironmentService getEnvironmentService() {
        return ServiceFactory.SERVICE_MANAGER.getEnvironmentService();
    }
    
    public static ConfigService getConfigService() {
        return ServiceFactory.SERVICE_MANAGER.getConfigService();
    }
    
    public static RPMConnectionService getRPMConnectionService() {
        return ServiceFactory.SERVICE_MANAGER.getRPMConnectionService();
    }
    
    public static IAgent getAgent() {
        return ServiceFactory.SERVICE_MANAGER.getAgent();
    }
    
    public static SamplerService getSamplerService() {
        return ServiceFactory.SERVICE_MANAGER.getSamplerService();
    }
    
    public static RPMServiceManager getRPMServiceManager() {
        return ServiceFactory.SERVICE_MANAGER.getRPMServiceManager();
    }
    
    public static IRPMService getRPMService() {
        return ServiceFactory.SERVICE_MANAGER.getRPMServiceManager().getRPMService();
    }
    
    public static IRPMService getRPMService(final String appName) {
        return ServiceFactory.SERVICE_MANAGER.getRPMServiceManager().getRPMService(appName);
    }
    
    public static CommandParser getCommandParser() {
        return ServiceFactory.SERVICE_MANAGER.getCommandParser();
    }
    
    public static JmxService getJmxService() {
        return ServiceFactory.SERVICE_MANAGER.getJmxService();
    }
    
    public static TransactionEventsService getTransactionEventsService() {
        return ServiceFactory.SERVICE_MANAGER.getTransactionEventsService();
    }
    
    public static SqlTraceService getSqlTraceService() {
        return ServiceFactory.SERVICE_MANAGER.getSqlTraceService();
    }
    
    public static BrowserService getBeaconService() {
        return ServiceFactory.SERVICE_MANAGER.getBrowserService();
    }
    
    public static CacheService getCacheService() {
        return ServiceFactory.SERVICE_MANAGER.getCacheService();
    }
    
    public static NormalizationService getNormalizationService() {
        return ServiceFactory.SERVICE_MANAGER.getNormalizationService();
    }
    
    public static DatabaseService getDatabaseService() {
        return ServiceFactory.SERVICE_MANAGER.getDatabaseService();
    }
    
    public static TransactionService getTransactionService() {
        return ServiceFactory.SERVICE_MANAGER.getTransactionService();
    }
    
    public static HarvestService getHarvestService() {
        return ServiceFactory.SERVICE_MANAGER.getHarvestService();
    }
    
    public static RemoteInstrumentationService getRemoteInstrumentationService() {
        return ServiceFactory.SERVICE_MANAGER.getRemoteInstrumentationService();
    }
    
    public static ThreadService getThreadService() {
        return ServiceFactory.SERVICE_MANAGER.getThreadService();
    }
    
    public static TransactionTraceService getTransactionTraceService() {
        return ServiceFactory.SERVICE_MANAGER.getTransactionTraceService();
    }
    
    public static TracerService getTracerService() {
        return ServiceFactory.SERVICE_MANAGER.getTracerService();
    }
    
    public static ProfilerService getProfilerService() {
        return ServiceFactory.SERVICE_MANAGER.getProfilerService();
    }
    
    public static ExtensionService getExtensionService() {
        return ServiceFactory.SERVICE_MANAGER.getExtensionService();
    }
    
    public static JarCollectorService getJarCollectorService() {
        return ServiceFactory.SERVICE_MANAGER.getJarCollectorService();
    }
    
    public static IXRaySessionService getXRaySessionService() {
        return ServiceFactory.SERVICE_MANAGER.getXRaySessionService();
    }
    
    public static AttributesService getAttributesService() {
        return ServiceFactory.SERVICE_MANAGER.getAttributesService();
    }
    
    public static Service getService(final String name) {
        return ServiceFactory.SERVICE_MANAGER.getService(name);
    }
    
    public static void addService(final Service service) {
        ServiceFactory.SERVICE_MANAGER.addService(service);
    }
    
    public static Map<String, Map<String, Object>> getServicesConfiguration() {
        return ServiceFactory.SERVICE_MANAGER.getServicesConfiguration();
    }
    
    public static AsyncTransactionService getAsyncTxService() {
        return ServiceFactory.SERVICE_MANAGER.getAsyncTxService();
    }
    
    public static UtilizationService getUtilizationService() {
        return ServiceFactory.SERVICE_MANAGER.getUtilizationService();
    }
}

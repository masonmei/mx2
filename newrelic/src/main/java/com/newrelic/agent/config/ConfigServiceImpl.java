// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.IRPMService;
import java.util.logging.Level;
import java.util.Iterator;
import com.newrelic.agent.logging.AgentLogManager;
import com.newrelic.agent.Agent;
import java.util.HashMap;
import java.text.MessageFormat;
import com.newrelic.agent.service.ServiceFactory;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import java.io.File;
import java.util.List;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.ConnectionListener;
import com.newrelic.agent.service.AbstractService;

public class ConfigServiceImpl extends AbstractService implements ConfigService, ConnectionListener, HarvestListener
{
    private static final String SANITIZED_SETTING = "****";
    private final List<AgentConfigListener> listeners2;
    private final File configFile;
    private final Map<String, Object> localSettings;
    private long lastModified;
    private final ConcurrentMap<String, AgentConfig> agentConfigs;
    private volatile AgentConfig defaultAgentConfig;
    private volatile AgentConfig localAgentConfig;
    private final String defaultAppName;
    
    protected ConfigServiceImpl(final AgentConfig config, final File configFile, final Map<String, Object> localSettings) {
        super(ConfigService.class.getSimpleName());
        this.listeners2 = new CopyOnWriteArrayList<AgentConfigListener>();
        this.agentConfigs = new ConcurrentHashMap<String, AgentConfig>();
        this.configFile = configFile;
        this.localSettings = Collections.unmodifiableMap((Map<? extends String, ?>)localSettings);
        this.localAgentConfig = config;
        this.defaultAgentConfig = this.localAgentConfig;
        this.defaultAppName = this.defaultAgentConfig.getApplicationName();
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() {
        ServiceFactory.getRPMServiceManager().addConnectionListener(this);
        if (this.configFile != null) {
            this.lastModified = this.configFile.lastModified();
            final String msg = MessageFormat.format("Configuration file is {0}", this.configFile.getAbsolutePath());
            this.getLogger().info(msg);
        }
        final Object apdex_t = this.localAgentConfig.getProperty("apdex_t", (Object)null);
        if (apdex_t != null) {
            final String msg2 = "The apdex_t setting is obsolete and is ignored! Set the apdex_t value for an application in New Relic UI";
            this.getLogger().warning(msg2);
        }
        final Object wait_for_customer_ssl = this.localAgentConfig.getProperty("wait_for_customer_ssl", (Object)null);
        if (wait_for_customer_ssl != null) {
            final String msg3 = "The wait_for_customer_ssl setting is obsolete and is ignored!";
            this.getLogger().warning(msg3);
        }
        ServiceFactory.getHarvestService().addHarvestListener(this);
    }
    
    protected void doStop() {
        ServiceFactory.getRPMServiceManager().removeConnectionListener(this);
        ServiceFactory.getHarvestService().removeHarvestListener(this);
    }
    
    public void addIAgentConfigListener(final AgentConfigListener listener) {
        this.listeners2.add(listener);
    }
    
    public void removeIAgentConfigListener(final AgentConfigListener listener) {
        this.listeners2.remove(listener);
    }
    
    public Map<String, Object> getLocalSettings() {
        return this.localSettings;
    }
    
    public Map<String, Object> getSanitizedLocalSettings() {
        final Map<String, Object> settings = new HashMap<String, Object>(this.localSettings);
        if (settings.containsKey("proxy_host")) {
            settings.put("proxy_host", "****");
        }
        if (settings.containsKey("proxy_user")) {
            settings.put("proxy_user", "****");
        }
        if (settings.containsKey("proxy_password")) {
            settings.put("proxy_password", "****");
        }
        return settings;
    }
    
    public AgentConfig getDefaultAgentConfig() {
        return this.defaultAgentConfig;
    }
    
    public AgentConfig getLocalAgentConfig() {
        return this.localAgentConfig;
    }
    
    public AgentConfig getAgentConfig(final String appName) {
        return this.getOrCreateAgentConfig(appName);
    }
    
    public TransactionTracerConfig getTransactionTracerConfig(final String appName) {
        return this.getOrCreateAgentConfig(appName).getTransactionTracerConfig();
    }
    
    public ErrorCollectorConfig getErrorCollectorConfig(final String appName) {
        return this.getOrCreateAgentConfig(appName).getErrorCollectorConfig();
    }
    
    public JarCollectorConfig getJarCollectorConfig(final String appName) {
        return this.getOrCreateAgentConfig(appName).getJarCollectorConfig();
    }
    
    public StripExceptionConfig getStripExceptionConfig(final String appName) {
        return this.getOrCreateAgentConfig(appName).getStripExceptionConfig();
    }
    
    private void checkConfigFile() throws Exception {
        if (this.configFile.lastModified() == this.lastModified) {
            return;
        }
        Agent.LOG.info("Re-reading New Relic configuration file");
        this.lastModified = this.configFile.lastModified();
        final Map<String, Object> settings = AgentConfigHelper.getConfigurationFileSettings(this.configFile);
        final AgentConfig agentConfig = this.createAgentConfig(this.defaultAppName, settings, null);
        final Map<String, Object> settings2 = AgentConfigFactory.createMap(this.localSettings);
        settings2.put("audit_mode", agentConfig.isAuditMode());
        settings2.put("log_level", agentConfig.getLogLevel());
        this.localAgentConfig = AgentConfigFactory.createAgentConfig(settings2, null);
        AgentLogManager.setLogLevel(agentConfig.getLogLevel());
        this.notifyListeners2(this.defaultAppName, agentConfig);
    }
    
    private void notifyListeners2(final String appName, final AgentConfig agentConfig) {
        for (final AgentConfigListener listener : this.listeners2) {
            listener.configChanged(appName, agentConfig);
        }
    }
    
    private AgentConfig getOrCreateAgentConfig(final String appName) {
        AgentConfig agentConfig = this.findAgentConfig(appName);
        if (agentConfig != null) {
            return agentConfig;
        }
        agentConfig = AgentConfigFactory.createAgentConfig(this.localSettings, null);
        final AgentConfig oldAgentConfig = this.agentConfigs.putIfAbsent(appName, agentConfig);
        return (oldAgentConfig == null) ? agentConfig : oldAgentConfig;
    }
    
    private AgentConfig findAgentConfig(final String appName) {
        if (appName == null || appName.equals(this.defaultAppName)) {
            return this.defaultAgentConfig;
        }
        return this.agentConfigs.get(appName);
    }
    
    private AgentConfig createAgentConfig(final String appName, final Map<String, Object> localSettings, final Map<String, Object> serverData) {
        try {
            return AgentConfigFactory.createAgentConfig(localSettings, serverData);
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Error configuring application \"{0}\" with server data \"{1}\": {2}", appName, serverData, e);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg, e);
            }
            else {
                Agent.LOG.warning(msg);
            }
            return null;
        }
    }
    
    private void replaceServerConfig(final String appName, final Map<String, Object> serverData) {
        if (Agent.LOG.isLoggable(Level.FINER)) {
            Agent.LOG.finer(MessageFormat.format("Received New Relic data for {0}: {1}", appName, serverData));
        }
        final AgentConfig agentConfig = this.createAgentConfig(appName, this.localSettings, serverData);
        if (agentConfig == null) {
            return;
        }
        if (appName == null || appName.equals(this.defaultAppName)) {
            this.defaultAgentConfig = agentConfig;
        }
        else {
            this.agentConfigs.put(appName, agentConfig);
        }
        this.logIfHighSecurityServerAndLocal(appName, agentConfig, serverData);
        this.notifyListeners2(appName, agentConfig);
    }
    
    private void logIfHighSecurityServerAndLocal(final String appName, final AgentConfig agentConfig, final Map<String, Object> serverData) {
        if (agentConfig.isHighSecurity() && serverData.containsKey("high_security")) {
            final String msg = MessageFormat.format("The agent is in high security mode for {0}: {1} setting is \"{2}\". {3} setting is \"{4}\". Disabling the collection of request parameters, message queue parameters, and user attributes.", appName, "transaction_tracer.record_sql", agentConfig.getTransactionTracerConfig().getRecordSql(), "ssl", agentConfig.isSSL());
            Agent.LOG.info(msg);
        }
    }
    
    public void connected(final IRPMService rpmService, final Map<String, Object> serverData) {
        final String appName = rpmService.getApplicationName();
        this.replaceServerConfig(appName, serverData);
    }
    
    public void disconnected(final IRPMService rpmService) {
    }
    
    public void afterHarvest(final String appName) {
        if (!appName.equals(this.defaultAppName)) {
            return;
        }
        try {
            this.checkConfigFile();
        }
        catch (Throwable t) {
            final String msg = MessageFormat.format("Unexpected exception checking for config file changes: {0}", t.toString());
            this.getLogger().warning(msg);
        }
        ServiceFactory.getClassTransformerService().checkShutdown();
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
    }
}

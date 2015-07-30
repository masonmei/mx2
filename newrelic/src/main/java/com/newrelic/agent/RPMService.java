// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

import com.newrelic.agent.environment.AgentIdentity;
import java.net.ConnectException;
import com.newrelic.agent.metric.MetricName;
import com.newrelic.agent.deps.org.json.simple.JSONObject;
import java.util.concurrent.TimeUnit;
import com.newrelic.agent.normalization.Normalizer;
import com.newrelic.agent.deps.org.apache.http.conn.HttpHostConnectException;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.agent.trace.TransactionTrace;
import com.newrelic.agent.sql.SqlTrace;
import com.newrelic.agent.service.analytics.CustomInsightsEvent;
import com.newrelic.agent.transport.HttpError;
import com.newrelic.agent.service.analytics.TransactionEvent;
import com.newrelic.agent.service.module.Jar;
import com.newrelic.agent.profile.IProfile;
import com.newrelic.agent.errors.TracedError;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.rmi.UnexpectedException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Arrays;
import com.newrelic.agent.config.BrowserMonitoringConfig;
import com.newrelic.agent.config.AgentJarHelper;
import com.newrelic.agent.config.SystemPropertyFactory;
import com.newrelic.agent.utilization.UtilizationData;
import com.newrelic.agent.environment.Environment;
import com.newrelic.agent.config.Hostname;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.transport.DataSenderFactory;
import com.newrelic.agent.service.ServiceFactory;
import java.util.concurrent.atomic.AtomicInteger;
import com.newrelic.agent.metric.MetricIdRegistry;
import com.newrelic.agent.transport.DataSender;
import java.util.List;
import com.newrelic.agent.errors.ErrorService;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.environment.EnvironmentChangeListener;
import com.newrelic.agent.service.AbstractService;

public class RPMService extends AbstractService implements IRPMService, EnvironmentChangeListener, AgentConfigListener
{
    public static final String COLLECT_TRACES_KEY = "collect_traces";
    public static final String COLLECT_ERRORS_KEY = "collect_errors";
    public static final String DATA_REPORT_PERIOD_KEY = "data_report_period";
    public static final String TRANSACTION_NAME_NAMING_SCHEME_KEY = "transaction_name.naming_scheme";
    public static final String SUPPORTABILITY_METRIC_HARVEST_INTERVAL = "Supportability/MetricHarvest/interval";
    public static final String SUPPORTABILITY_METRIC_HARVEST_TRANSMIT = "Supportability/MetricHarvest/transmit";
    public static final String SUPPORTABILITY_METRIC_HARVEST_COUNT = "Supportability/MetricHarvest/count";
    public static final String AGENT_METRICS_COUNT = "Agent/Metrics/Count";
    public static final int DEFAULT_REQUEST_TIMEOUT_IN_SECONDS = 120;
    public static final String FRAMEWORK_TRANSACTION_NAMING_SCHEME = "framework";
    private static final int MESSAGE_LIMIT_PER_PERIOD = 20;
    private static final int LOG_MESSAGE_COUNT = 5;
    private static final String INTERMITTENT_503_MESSAGE = "Server returned HTTP response code: 503";
    private final String host;
    private final int port;
    private volatile boolean connected;
    private final ErrorService errorService;
    private long lastReportTime;
    private final String appName;
    private final List<String> appNames;
    private final ConnectionListener connectionListener;
    private final boolean isMainApp;
    private volatile boolean hasEverConnected;
    private volatile String transactionNamingScheme;
    private final DataSender dataSender;
    private final MetricIdRegistry metricIdRegistry;
    private long connectionTimestamp;
    private final AtomicInteger last503Error;
    private final AtomicInteger retryCount;
    
    public RPMService(final List<String> appNames, final ConnectionListener connectionListener) {
        super(RPMService.class.getSimpleName() + "/" + appNames.get(0));
        this.connected = false;
        this.hasEverConnected = false;
        this.metricIdRegistry = new MetricIdRegistry();
        this.connectionTimestamp = 0L;
        this.last503Error = new AtomicInteger(0);
        this.retryCount = new AtomicInteger(0);
        this.appName = appNames.get(0).intern();
        final AgentConfig config = ServiceFactory.getConfigService().getAgentConfig(this.appName);
        this.dataSender = DataSenderFactory.create(config);
        this.appNames = appNames;
        this.connectionListener = connectionListener;
        this.lastReportTime = System.currentTimeMillis();
        this.errorService = new ErrorService(this.appName);
        this.host = config.getHost();
        this.port = config.getPort();
        this.isMainApp = this.appName.equals(config.getApplicationName());
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    protected void doStart() {
        this.connect();
        ServiceFactory.getEnvironmentService().getEnvironment().addEnvironmentChangeListener(this);
        ServiceFactory.getConfigService().addIAgentConfigListener(this);
        ServiceFactory.getServiceManager().getCircuitBreakerService().addRPMService(this);
    }
    
    private Boolean getAndLogHighSecurity(final AgentConfig config) {
        final boolean isHighSec = config.isHighSecurity();
        if (isHighSec) {
            Agent.LOG.log(Level.INFO, "High security is configured locally for application {0}.", new Object[] { this.appName });
        }
        return isHighSec;
    }
    
    protected Map<String, Object> getStartOptions() {
        final AgentConfig agentConfig = ServiceFactory.getConfigService().getAgentConfig(this.appName);
        final int pid = ServiceFactory.getEnvironmentService().getProcessPID();
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put("pid", pid);
        final String language = agentConfig.getLanguage();
        options.put("language", language);
        final String defaultHost = Hostname.getHostname(Agent.LOG, agentConfig);
        options.put("host", defaultHost);
        options.put("display_host", Hostname.getDisplayHostname(Agent.LOG, agentConfig, defaultHost, this.appName));
        options.put("high_security", this.getAndLogHighSecurity(agentConfig));
        final Environment environment = ServiceFactory.getEnvironmentService().getEnvironment();
        options.put("environment", environment);
        if (agentConfig.getProperty("send_environment_info", true)) {
            options.put("settings", this.getSettings());
        }
        final UtilizationData utilizationData = ServiceFactory.getUtilizationService().updateUtilizationData();
        options.put("utilization", utilizationData.map());
        final String instanceName = environment.getAgentIdentity().getInstanceName();
        if (instanceName != null) {
            options.put("instance_name", instanceName);
        }
        options.put("agent_version", Agent.getVersion());
        options.put("app_name", this.appNames);
        final StringBuilder identifier = new StringBuilder(language);
        identifier.append(':').append(this.appName);
        final Integer serverPort = environment.getAgentIdentity().getServerPort();
        if (serverPort != null) {
            identifier.append(':').append(serverPort);
        }
        options.put("identifier", identifier.toString());
        options.put("labels", agentConfig.getLabelsConfig());
        return options;
    }
    
    private Map<String, Object> getSettings() {
        final Map<String, Object> localSettings = ServiceFactory.getConfigService().getSanitizedLocalSettings();
        final Map<String, Object> settings = new HashMap<String, Object>(localSettings);
        final Map<String, String> props = SystemPropertyFactory.getSystemPropertyProvider().getNewRelicSystemProperties();
        if (!props.isEmpty()) {
            settings.put("system", props);
        }
        final BrowserMonitoringConfig browserConfig = ServiceFactory.getConfigService().getAgentConfig(this.appName).getBrowserMonitoringConfig();
        settings.put("browser_monitoring.loader", browserConfig.getLoaderType());
        settings.put("browser_monitoring.debug", browserConfig.isDebug());
        final String buildDate = AgentJarHelper.getBuildDate();
        if (buildDate != null) {
            settings.put("build_date", buildDate);
        }
        settings.put("services", ServiceFactory.getServicesConfiguration());
        return settings;
    }
    
    public synchronized Map<String, Object> launch() throws Exception {
        if (this.isConnected()) {
            return null;
        }
        Map<String, Object> data = null;
        try {
            data = this.dataSender.connect(this.getStartOptions());
        }
        catch (ForceDisconnectException e) {
            this.logForceDisconnectException(e);
            this.shutdownAsync();
            throw e;
        }
        Agent.LOG.log(Level.FINER, "Connection response : {0}", new Object[] { data });
        final List<String> requiredParams = Arrays.asList("collect_errors", "collect_traces", "data_report_period");
        if (!data.keySet().containsAll(requiredParams)) {
            throw new UnexpectedException(MessageFormat.format("Missing the following connection parameters", requiredParams.removeAll(data.keySet())));
        }
        Agent.LOG.log(Level.INFO, "Agent {0} connected to {1}", new Object[] { this.toString(), this.getHostString() });
        this.transactionNamingScheme = data.get("transaction_name.naming_scheme");
        if (this.transactionNamingScheme != null) {
            this.transactionNamingScheme = this.transactionNamingScheme.intern();
            Agent.LOG.log(Level.INFO, "Setting: {0} to: {1}", new Object[] { "transaction_name.naming_scheme", this.transactionNamingScheme });
        }
        try {
            this.logCollectorMessages(data);
        }
        catch (Exception ex) {
            Agent.LOG.log(Level.FINEST, (Throwable)ex, "Error processing collector connect messages", new Object[0]);
        }
        this.connectionTimestamp = System.nanoTime();
        this.connected = true;
        this.hasEverConnected = true;
        if (this.connectionListener != null) {
            this.connectionListener.connected(this, data);
        }
        return data;
    }
    
    private void logCollectorMessages(final Map<String, Object> data) {
        final List<Map<String, String>> messages = data.get("messages");
        if (messages != null) {
            for (final Map<String, String> message : messages) {
                final String level = message.get("level");
                final String text = message.get("message");
                Agent.LOG.log(Level.parse(level), text);
            }
        }
    }
    
    public String getTransactionNamingScheme() {
        return this.transactionNamingScheme;
    }
    
    private void logForceDisconnectException(final ForceDisconnectException e) {
        Agent.LOG.log(Level.INFO, "Received a ForceDisconnectException: {0}", new Object[] { e.toString() });
    }
    
    private void shutdownAsync() {
        ServiceFactory.getAgent().shutdownAsync();
    }
    
    private void logForceRestartException(final ForceRestartException e) {
        Agent.LOG.log(Level.INFO, "Received a ForceRestartException: {0}", new Object[] { e.toString() });
    }
    
    private void reconnectSync() throws Exception {
        this.disconnect();
        this.launch();
    }
    
    private void reconnectAsync() {
        this.disconnect();
        ServiceFactory.getRPMConnectionService().connectImmediate(this);
    }
    
    private void disconnect() {
        this.connected = false;
        this.metricIdRegistry.clear();
    }
    
    public synchronized void reconnect() {
        Agent.LOG.log(Level.INFO, "{0} is reconnecting", new Object[] { this.getApplicationName() });
        try {
            this.shutdown();
            this.reconnectAsync();
        }
        catch (Exception e) {
            this.reconnectAsync();
        }
        finally {
            this.reconnectAsync();
        }
    }
    
    public String getHostString() {
        return MessageFormat.format("{0}:{1}", this.host, Integer.toString(this.port));
    }
    
    public String toString() {
        final StringBuilder builder = new StringBuilder(ManagementFactory.getRuntimeMXBean().getName());
        builder.append('/').append(this.appName);
        return builder.toString();
    }
    
    private void sendErrorData(final List<TracedError> errors) {
        Agent.LOG.log(Level.FINE, "Sending {0} error(s)", new Object[] { errors.size() });
        try {
            this.dataSender.sendErrorData(errors);
        }
        catch (IgnoreSilentlyException e4) {}
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
        }
        catch (Exception e3) {
            final String msg = MessageFormat.format("Error sending error data to New Relic: {0}", e3);
            if (Agent.LOG.isLoggable(Level.FINER)) {
                Agent.LOG.log(Level.FINER, msg, e3);
            }
            else {
                Agent.LOG.warning(msg);
            }
        }
    }
    
    public List<Long> sendProfileData(final List<IProfile> profiles) throws Exception {
        Agent.LOG.log(Level.INFO, "Sending {0} profile(s)", new Object[] { profiles.size() });
        try {
            return this.sendProfileDataSyncRestart(profiles);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    private List<Long> sendProfileDataSyncRestart(final List<IProfile> profiles) throws Exception {
        try {
            return this.dataSender.sendProfileData(profiles);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            return this.dataSender.sendProfileData(profiles);
        }
    }
    
    public void sendModules(final List<Jar> pJarsToSend) throws Exception {
        Agent.LOG.log(Level.FINE, "Sending {0} module(s)", new Object[] { pJarsToSend.size() });
        try {
            this.sendModulesSyncRestart(pJarsToSend);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    private void sendModulesSyncRestart(final List<Jar> pJarsToSend) throws Exception {
        try {
            this.dataSender.sendModules(pJarsToSend);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            this.dataSender.sendModules(pJarsToSend);
        }
    }
    
    public void sendAnalyticsEvents(final Collection<TransactionEvent> events) throws Exception {
        Agent.LOG.log(Level.FINE, "Sending {0} analytics event(s)", new Object[] { events.size() });
        try {
            this.sendAnalyticsEventsSyncRestart(events);
        }
        catch (HttpError e) {
            if (e.getStatusCode() != 413 && e.getStatusCode() != 415) {
                throw e;
            }
        }
        catch (ForceRestartException e2) {
            this.logForceRestartException(e2);
            this.reconnectAsync();
            throw e2;
        }
        catch (ForceDisconnectException e3) {
            this.logForceDisconnectException(e3);
            this.shutdownAsync();
            throw e3;
        }
    }
    
    private void sendAnalyticsEventsSyncRestart(final Collection<TransactionEvent> events) throws Exception {
        try {
            this.dataSender.sendAnalyticsEvents(events);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            this.dataSender.sendAnalyticsEvents(events);
        }
    }
    
    public void sendCustomAnalyticsEvents(final Collection<CustomInsightsEvent> events) throws Exception {
        Agent.LOG.log(Level.FINE, "Sending {0} analytics event(s)", new Object[] { events.size() });
        try {
            this.sendCustomAnalyticsEventsSyncRestart(events);
        }
        catch (HttpError e) {
            if (e.getStatusCode() != 413 && e.getStatusCode() != 415) {
                throw e;
            }
        }
        catch (ForceRestartException e2) {
            this.logForceRestartException(e2);
            this.reconnectAsync();
            throw e2;
        }
        catch (ForceDisconnectException e3) {
            this.logForceDisconnectException(e3);
            this.shutdownAsync();
            throw e3;
        }
    }
    
    private void sendCustomAnalyticsEventsSyncRestart(final Collection<CustomInsightsEvent> events) throws Exception {
        try {
            this.dataSender.sendCustomAnalyticsEvents(events);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            this.dataSender.sendCustomAnalyticsEvents(events);
        }
    }
    
    public void sendSqlTraceData(final List<SqlTrace> sqlTraces) throws Exception {
        Agent.LOG.log(Level.FINE, "Sending {0} sql trace(s)", new Object[] { sqlTraces.size() });
        try {
            this.sendSqlTraceDataSyncRestart(sqlTraces);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    private void sendSqlTraceDataSyncRestart(final List<SqlTrace> sqlTraces) throws Exception {
        try {
            this.dataSender.sendSqlTraceData(sqlTraces);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            this.dataSender.sendSqlTraceData(sqlTraces);
        }
    }
    
    public void sendTransactionTraceData(final List<TransactionTrace> traces) throws Exception {
        Agent.LOG.log(Level.FINE, "Sending {0} trace(s)", new Object[] { traces.size() });
        try {
            this.sendTransactionTraceDataSyncRestart(traces);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    private void sendTransactionTraceDataSyncRestart(final List<TransactionTrace> traces) throws Exception {
        try {
            this.dataSender.sendTransactionTraceData(traces);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            this.dataSender.sendTransactionTraceData(traces);
        }
    }
    
    public ErrorService getErrorService() {
        return this.errorService;
    }
    
    public String getApplicationName() {
        return this.appName;
    }
    
    public boolean isMainApp() {
        return this.isMainApp;
    }
    
    public synchronized void shutdown() throws Exception {
        try {
            if (this.isConnected()) {
                this.dataSender.shutdown(System.currentTimeMillis());
            }
            this.disconnect();
        }
        finally {
            this.disconnect();
        }
    }
    
    public List<List<?>> getAgentCommands() throws Exception {
        try {
            return this.getAgentCommandsSyncRestart();
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    public Collection<?> getXRaySessionInfo(final Collection<Long> newIds) throws Exception {
        try {
            return this.dataSender.getXRayParameters(newIds);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    private List<List<?>> getAgentCommandsSyncRestart() throws Exception {
        try {
            return this.dataSender.getAgentCommands();
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            return this.dataSender.getAgentCommands();
        }
    }
    
    public void sendCommandResults(final Map<Long, Object> commandResults) throws Exception {
        try {
            this.sendCommandResultsSyncRestart(commandResults);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    private void sendCommandResultsSyncRestart(final Map<Long, Object> commandResults) throws Exception {
        try {
            this.dataSender.sendCommandResults(commandResults);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            this.dataSender.sendCommandResults(commandResults);
        }
    }
    
    public void queuePingCommand() throws Exception {
        try {
            this.queuePingCommandSyncRestart();
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectAsync();
            throw e;
        }
        catch (ForceDisconnectException e2) {
            this.logForceDisconnectException(e2);
            this.shutdownAsync();
            throw e2;
        }
    }
    
    private void queuePingCommandSyncRestart() throws Exception {
        try {
            this.dataSender.queuePingCommand();
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            this.dataSender.queuePingCommand();
        }
    }
    
    public void connect() {
        ServiceFactory.getRPMConnectionService().connect(this);
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public boolean hasEverConnected() {
        return this.hasEverConnected;
    }
    
    public void harvest(final StatsEngine statsEngine) {
        final List<TracedError> errors = this.errorService.harvest(this, statsEngine);
        if (!this.isConnected()) {
            try {
                Agent.LOG.fine("Trying to re-establish connection to New Relic.");
                this.launch();
            }
            catch (Exception e) {
                Agent.LOG.fine("Problem trying to re-establish connection to New Relic: " + e.getMessage());
            }
        }
        if (this.isConnected()) {
            boolean retry = false;
            if (this.metricIdRegistry.getSize() > 1000) {
                statsEngine.getStats("Agent/Metrics/Count").setCallCount(this.metricIdRegistry.getSize());
            }
            final Normalizer metricNormalizer = ServiceFactory.getNormalizationService().getMetricNormalizer(this.appName);
            final List<MetricData> data = statsEngine.getMetricData(metricNormalizer, this.metricIdRegistry);
            final long startTime = System.nanoTime();
            long reportInterval = 0L;
            try {
                final long now = System.currentTimeMillis();
                final List<List<?>> responseList = this.sendMetricDataSyncRestart(this.lastReportTime, now, data);
                reportInterval = now - this.lastReportTime;
                this.lastReportTime = now;
                this.registerMetricIds(responseList);
                this.last503Error.set(0);
                if (this.retryCount.get() > 0) {
                    Agent.LOG.log(Level.INFO, "Successfully reconnected to the New Relic data service.");
                }
                Agent.LOG.log(Level.FINE, "Reported {0} timeslices for {1}", new Object[] { data.size(), this.getApplicationName() });
                this.sendErrorData(errors);
            }
            catch (InternalLimitExceeded e8) {
                Agent.LOG.log(Level.SEVERE, "The metric data post was too large.  {0} timeslices will not be resent", new Object[] { data.size() });
            }
            catch (MetricDataException e2) {
                Agent.LOG.log(Level.SEVERE, "An invalid response was received while sending metric data. This data will not be resent.");
                Agent.LOG.log(Level.FINEST, (Throwable)e2, e2.toString(), new Object[0]);
            }
            catch (HttpError e3) {
                retry = e3.isRetryableError();
                if (503 == e3.getStatusCode()) {
                    this.handle503Error(e3);
                }
                else if (retry) {
                    Agent.LOG.log(Level.INFO, "An error occurred posting metric data - {0}.  This data will be resent later.", new Object[] { e3.getMessage() });
                }
                else {
                    Agent.LOG.log(Level.SEVERE, "An error occurred posting metric data - {0}.  {1} timeslices will not be resent.", new Object[] { e3.getMessage(), data.size() });
                }
            }
            catch (ForceRestartException e4) {
                this.logForceRestartException(e4);
                this.reconnectAsync();
                retry = true;
            }
            catch (ForceDisconnectException e5) {
                this.logForceDisconnectException(e5);
                this.shutdownAsync();
            }
            catch (HttpHostConnectException e6) {
                retry = true;
                Agent.LOG.log(Level.INFO, "An connection error occurred contacting {0}.  Please check your network / proxy settings.", new Object[] { e6.getHost() });
                Agent.LOG.log(Level.FINEST, (Throwable)e6, e6.toString(), new Object[0]);
            }
            catch (Exception e7) {
                this.logMetricDataError(e7);
                retry = true;
                final String message = e7.getMessage().toLowerCase();
                if (message.contains("json") && message.contains("parse")) {
                    retry = false;
                }
            }
            final long duration = System.nanoTime() - startTime;
            if (retry) {
                this.retryCount.getAndIncrement();
            }
            else {
                this.retryCount.set(0);
                statsEngine.clear();
                this.recordSupportabilityMetrics(statsEngine, reportInterval, duration, data.size());
            }
        }
    }
    
    private void recordSupportabilityMetrics(final StatsEngine statsEngine, final long reportInterval, final long duration, final int dataSize) {
        if (reportInterval > 0L) {
            statsEngine.getResponseTimeStats("Supportability/MetricHarvest/interval").recordResponseTime(reportInterval, TimeUnit.MILLISECONDS);
        }
        statsEngine.getResponseTimeStats("Supportability/MetricHarvest/transmit").recordResponseTime(duration, TimeUnit.NANOSECONDS);
        statsEngine.getStats("Supportability/MetricHarvest/count").incrementCallCount(dataSize);
    }
    
    private List<List<?>> sendMetricDataSyncRestart(final long beginTimeMillis, final long endTimeMillis, final List<MetricData> metricData) throws Exception {
        try {
            return this.dataSender.sendMetricData(beginTimeMillis, endTimeMillis, metricData);
        }
        catch (ForceRestartException e) {
            this.logForceRestartException(e);
            this.reconnectSync();
            return this.dataSender.sendMetricData(beginTimeMillis, endTimeMillis, metricData);
        }
    }
    
    private void registerMetricIds(final List<List<?>> responseList) {
        for (final List<?> response : responseList) {
            final JSONObject jsonObj = JSONObject.class.cast(response.get(0));
            final MetricName metricName = MetricName.parseJSON(jsonObj);
            final Long id = Long.class.cast(response.get(1));
            this.metricIdRegistry.setMetricId(metricName, (int)(Object)id);
        }
    }
    
    private void logMetricDataError(final Exception e) {
        Agent.LOG.log(Level.INFO, "An unexpected error occurred sending metric data to New Relic.  Please file a support ticket once you have seen several of these messages in a short period of time: {0}", new Object[] { e.toString() });
        Agent.LOG.log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
    }
    
    private void handle503Error(final Exception e) {
        final String msg = "A 503 (Unavailable) response was received while sending metric data to New Relic.  The agent will continue to aggregate data and report it in the next time period.";
        if (this.last503Error.getAndIncrement() == 5) {
            Agent.LOG.info(msg);
            Agent.LOG.log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
        }
        else {
            Agent.LOG.log(Level.FINER, msg, e);
        }
    }
    
    protected void doStop() {
        try {
            this.shutdown();
        }
        catch (Exception e) {
            final Level level = (e instanceof ConnectException) ? Level.FINER : Level.SEVERE;
            Agent.LOG.log(level, "An error occurred in the NewRelic agent shutdown", e);
        }
        ServiceFactory.getEnvironmentService().getEnvironment().removeEnvironmentChangeListener(this);
        ServiceFactory.getConfigService().removeIAgentConfigListener(this);
        ServiceFactory.getServiceManager().getCircuitBreakerService().removeRPMService(this);
    }
    
    public long getConnectionTimestamp() {
        return this.connectionTimestamp;
    }
    
    public void agentIdentityChanged(final AgentIdentity agentIdentity) {
        if (this.connected) {
            this.logger.log(Level.FINE, "Reconnecting after an environment change");
            this.reconnect();
        }
    }
    
    public void configChanged(final String appName, final AgentConfig agentConfig) {
        this.last503Error.set(0);
    }
}

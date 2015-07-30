// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Iterator;
import java.util.Collections;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;

public class AgentConfigImpl extends BaseConfig implements AgentConfig
{
    public static final String APDEX_T = "apdex_t";
    public static final String API_HOST = "api_host";
    public static final String API_PORT = "api_port";
    public static final String APP_NAME = "app_name";
    public static final String AUDIT_MODE = "audit_mode";
    public static final String BROWSER_MONITORING = "browser_monitoring";
    public static final String ATTRIBUTES = "attributes";
    public static final String CAPTURE_PARAMS = "capture_params";
    public static final String CAPTURE_MESSAGING_PARAMS = "capture_messaging_params";
    public static final String CLASS_TRANSFORMER = "class_transformer";
    public static final String CPU_SAMPLING_ENABLED = "cpu_sampling_enabled";
    public static final String CROSS_APPLICATION_TRACER = "cross_application_tracer";
    public static final String DEBUG = "newrelic.debug";
    public static final String AGENT_ENABLED = "agent_enabled";
    public static final String ENABLED = "enabled";
    public static final String ENABLE_AUTO_APP_NAMING = "enable_auto_app_naming";
    public static final String ENABLE_AUTO_TRANSACTION_NAMING = "enable_auto_transaction_naming";
    public static final String ENABLE_BOOTSTRAP_CLASS_INSTRUMENTATION = "enable_bootstrap_class_instrumentation";
    public static final String ENABLE_CLASS_RETRANSFORMATION = "enable_class_retransformation";
    public static final String ENABLE_CUSTOM_TRACING = "enable_custom_tracing";
    public static final String ENABLE_SESSION_COUNT_TRACKING = "enable_session_count_tracking";
    public static final String ERROR_COLLECTOR = "error_collector";
    public static final String HIGH_SECURITY = "high_security";
    public static final String JMX = "jmx";
    public static final String JAR_COLLECTOR = "jar_collector";
    public static final String ANALYTICS_EVENTS = "analytics_events";
    public static final String TRANSACTION_EVENTS = "transaction_events";
    public static final String CUSTOM_INSIGHT_EVENTS = "custom_insights_events";
    public static final String USE_PRIVATE_SSL = "use_private_ssl";
    public static final String REINSTRUMENT = "reinstrument";
    public static final String XRAY_SESSIONS_ENABLED = "xray_session_enabled";
    public static final String PLATFORM_INFORMATION_ENABLED = "platform_information_enabled";
    public static final String IBM = "ibm";
    public static final String EXT_CONFIG_DIR = "extensions.dir";
    public static final String HOST = "host";
    public static final String IGNORE_JARS = "ignore_jars";
    public static final String IS_SSL = "ssl";
    public static final String LABELS = "labels";
    public static final String LANGUAGE = "language";
    public static final String LICENSE_KEY = "license_key";
    public static final String LOG_FILE_COUNT = "log_file_count";
    public static final String LOG_FILE_NAME = "log_file_name";
    public static final String LOG_FILE_PATH = "log_file_path";
    public static final String LOG_LEVEL = "log_level";
    public static final String LOG_LIMIT = "log_limit_in_kbytes";
    public static final String LOG_DAILY = "log_daily";
    public static final int MAX_USER_PARAMETERS = 64;
    public static final int MAX_USER_PARAMETER_SIZE = 255;
    public static final String KEY_TRANSACTIONS = "web_transactions_apdex";
    public static final String PORT = "port";
    public static final String PROXY_HOST = "proxy_host";
    public static final String PROXY_PORT = "proxy_port";
    public static final String PROXY_USER = "proxy_user";
    public static final String PROXY_PASS = "proxy_password";
    public static final String REPORT_SQL_PARSER_ERRORS = "report_sql_parser_errors";
    public static final String SEND_DATA_ON_EXIT = "send_data_on_exit";
    public static final String SEND_DATA_ON_EXIT_THRESHOLD = "send_data_on_exit_threshold";
    public static final String SEND_ENVIRONMENT_INFO = "send_environment_info";
    public static final String SEND_JVM_PROPERY = "send_jvm_props";
    public static final String SLOW_SQL = "slow_sql";
    public static final String STARTUP_LOG_LEVEL = "startup_log_level";
    public static final String STDOUT = "STDOUT";
    public static final String SYNC_STARTUP = "sync_startup";
    public static final String STARTUP_TIMING = "startup_timing";
    public static final String STRIP_EXCEPTION_MESSAGES = "strip_exception_messages";
    public static final String THREAD_PROFILER = "thread_profiler";
    public static final String TRANSACTION_SIZE_LIMIT = "transaction_size_limit";
    public static final String TRANSACTION_TRACER = "transaction_tracer";
    public static final String THREAD_CPU_TIME_ENABLED = "thread_cpu_time_enabled";
    public static final String THREAD_PROFILER_ENABLED = "enabled";
    public static final String TRACE_DATA_CALLS = "trace_data_calls";
    public static final String TRIM_STATS = "trim_stats";
    public static final String WAIT_FOR_RPM_CONNECT = "wait_for_rpm_connect";
    public static final double DEFAULT_APDEX_T = 1.0;
    public static final String DEFAULT_API_HOST = "rpm.newrelic.com";
    public static final boolean DEFAULT_AUDIT_MODE = false;
    public static final boolean DEFAULT_CPU_SAMPLING_ENABLED = true;
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_ENABLE_AUTO_APP_NAMING = false;
    public static final boolean DEFAULT_ENABLE_AUTO_TRANSACTION_NAMING = true;
    public static final boolean DEFAULT_ENABLE_CUSTOM_TRACING = true;
    public static final boolean DEFAULT_ENABLE_SESSION_COUNT_TRACKING = false;
    public static final boolean DEFAULT_HIGH_SECURITY = false;
    public static final boolean DEFAULT_PLATFORM_INFORMATION_ENABLED = true;
    public static final String DEFAULT_HOST = "collector.newrelic.com";
    public static final boolean DEFAULT_IS_SSL = true;
    public static final String DEFAULT_LANGUAGE = "java";
    public static final int DEFAULT_LOG_FILE_COUNT = 1;
    public static final String DEFAULT_LOG_FILE_NAME = "newrelic_agent.log";
    public static final String DEFAULT_LOG_LEVEL = "info";
    public static final int DEFAULT_LOG_LIMIT = 0;
    public static final boolean DEFAULT_LOG_DAILY = false;
    public static final int DEFAULT_PORT = 80;
    public static final String DEFAULT_PROXY_HOST;
    public static final int DEFAULT_PROXY_PORT = 8080;
    public static final boolean DEFAULT_REPORT_SQL_PARSER_ERRORS = false;
    public static final boolean DEFAULT_SEND_DATA_ON_EXIT = false;
    public static final int DEFAULT_SEND_DATA_ON_EXIT_THRESHOLD = 60;
    public static final boolean DEFAULT_SEND_ENVIRONMENT_INFO = true;
    public static final int DEFAULT_SSL_PORT = 443;
    public static final boolean DEFAULT_SYNC_STARTUP = false;
    public static final boolean DEFAULT_STARTUP_TIMING = true;
    public static final boolean DEFAULT_TRACE_DATA_CALLS = false;
    public static final int DEFAULT_TRANSACTION_SIZE_LIMIT = 2000;
    public static final boolean DEFAULT_TRIM_STATS = true;
    public static final boolean DEFAULT_WAIT_FOR_RPM_CONNECT = true;
    public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.";
    public static final boolean DEFAULT_USE_PRIVATE_SSL = false;
    public static final boolean DEFAULT_XRAY_SESSIONS_ENABLED = true;
    public static final String IBM_WORKAROUND = "ibm_iv25688_workaround";
    public static final boolean IBM_WORKAROUND_DEFAULT;
    public static final String GENERIC_JDBC_SUPPORT = "generic";
    public static final String MYSQL_JDBC_SUPPORT = "mysql";
    private final boolean highSecurity;
    private final long apdexTInMillis;
    private final boolean enabled;
    private final boolean debug;
    private final String licenseKey;
    private final String host;
    private final int port;
    private final Integer proxyPort;
    private final boolean isSSL;
    private final List<String> ignoreJars;
    private final String appName;
    private final List<String> appNames;
    private final boolean cpuSamplingEnabled;
    private final boolean autoAppNamingEnabled;
    private final boolean autoTransactionNamingEnabled;
    private final String logLevel;
    private final boolean logDaily;
    private final String proxyHost;
    private final String proxyUser;
    private final String proxyPass;
    private final boolean sessionCountTrackingEnabled;
    private final int transactionSizeLimit;
    private final boolean reportSqlParserErrors;
    private final boolean auditMode;
    private final boolean waitForRPMConnect;
    private final boolean startupTimingEnabled;
    private final TransactionTracerConfigImpl transactionTracerConfig;
    private final ErrorCollectorConfig errorCollectorConfig;
    private final SqlTraceConfig sqlTraceConfig;
    private final ThreadProfilerConfig threadProfilerConfig;
    private final TransactionTracerConfigImpl requestTransactionTracerConfig;
    private final TransactionTracerConfigImpl backgroundTransactionTracerConfig;
    private final BrowserMonitoringConfig browserMonitoringConfig;
    private final ClassTransformerConfig classTransformerConfig;
    private final KeyTransactionConfig keyTransactionConfig;
    private final JmxConfig jmxConfig;
    private final JarCollectorConfig jarCollectorConfig;
    private final ReinstrumentConfig reinstrumentConfig;
    private final CrossProcessConfig crossProcessConfig;
    private final LabelsConfig labelsConfig;
    private final CircuitBreakerConfig circuitBreakerConfig;
    private final StripExceptionConfig stripExceptionConfig;
    private final boolean isApdexTSet;
    private final boolean sendJvmProps;
    private final boolean usePrivateSSL;
    private final boolean xRaySessionsEnabled;
    private final boolean trimStats;
    private final boolean platformInformationEnabled;
    private final Map<String, Object> flattenedProperties;
    private final HashSet<String> jdbcSupport;
    private final boolean genericJdbcSupportEnabled;
    private final int maxStackTraceLines;
    private final boolean ibmWorkaroundEnabled;
    private final Config instrumentationConfig;
    
    private AgentConfigImpl(final Map<String, Object> props) {
        super(props, "newrelic.config.");
        this.highSecurity = this.getProperty("high_security", false);
        this.isSSL = this.initSsl(this.highSecurity, props);
        this.isApdexTSet = (this.getProperty("apdex_t") != null);
        this.apdexTInMillis = (long)(this.getDoubleProperty("apdex_t", 1.0) * 1000.0);
        this.debug = Boolean.getBoolean("newrelic.debug");
        this.enabled = (this.getProperty("enabled", true) && this.getProperty("agent_enabled", true));
        this.licenseKey = this.getProperty("license_key");
        this.host = this.getProperty("host", "collector.newrelic.com");
        this.ignoreJars = new ArrayList<String>(this.getUniqueStrings("ignore_jars", ","));
        this.logLevel = this.initLogLevel();
        this.logDaily = this.getProperty("log_daily", false);
        this.port = this.getIntProperty("port", this.isSSL ? 443 : 80);
        this.proxyHost = this.getProperty("proxy_host", AgentConfigImpl.DEFAULT_PROXY_HOST);
        this.proxyPort = this.getIntProperty("proxy_port", 8080);
        this.proxyUser = this.getProperty("proxy_user");
        this.proxyPass = this.getProperty("proxy_password");
        this.appNames = new ArrayList<String>(this.getUniqueStrings("app_name", ";"));
        this.appName = this.getFirstString("app_name", ";");
        this.cpuSamplingEnabled = this.getProperty("cpu_sampling_enabled", true);
        this.autoAppNamingEnabled = this.getProperty("enable_auto_app_naming", false);
        this.autoTransactionNamingEnabled = this.getProperty("enable_auto_transaction_naming", true);
        this.transactionSizeLimit = this.getIntProperty("transaction_size_limit", 2000) * 1024;
        this.sessionCountTrackingEnabled = this.getProperty("enable_session_count_tracking", false);
        this.reportSqlParserErrors = this.getProperty("report_sql_parser_errors", false);
        this.auditMode = (this.getProperty("trace_data_calls", false) || this.getProperty("audit_mode", false));
        this.waitForRPMConnect = this.getProperty("wait_for_rpm_connect", true);
        this.startupTimingEnabled = this.getProperty("startup_timing", true);
        this.transactionTracerConfig = this.initTransactionTracerConfig(this.apdexTInMillis, this.highSecurity);
        this.requestTransactionTracerConfig = this.transactionTracerConfig.createRequestTransactionTracerConfig(this.apdexTInMillis, this.highSecurity);
        this.backgroundTransactionTracerConfig = this.transactionTracerConfig.createBackgroundTransactionTracerConfig(this.apdexTInMillis, this.highSecurity);
        this.errorCollectorConfig = this.initErrorCollectorConfig();
        this.threadProfilerConfig = this.initThreadProfilerConfig();
        this.keyTransactionConfig = this.initKeyTransactionConfig(this.apdexTInMillis);
        this.sqlTraceConfig = this.initSqlTraceConfig();
        this.browserMonitoringConfig = this.initBrowserMonitoringConfig();
        this.classTransformerConfig = this.initClassTransformerConfig();
        this.crossProcessConfig = this.initCrossProcessConfig();
        this.stripExceptionConfig = this.initStripExceptionConfig(this.highSecurity);
        this.labelsConfig = new LabelsConfigImpl(this.getProperty("labels"));
        this.jmxConfig = this.initJmxConfig();
        this.jarCollectorConfig = this.initJarCollectorConfig();
        this.reinstrumentConfig = this.initReinstrumentConfig();
        this.circuitBreakerConfig = this.initCircuitBreakerConfig();
        this.sendJvmProps = this.getProperty("send_jvm_props", true);
        this.usePrivateSSL = this.getProperty("use_private_ssl", false);
        this.xRaySessionsEnabled = this.getProperty("xray_session_enabled", true);
        this.trimStats = this.getProperty("trim_stats", true);
        this.platformInformationEnabled = this.getProperty("platform_information_enabled", true);
        this.ibmWorkaroundEnabled = this.getProperty("ibm_iv25688_workaround", AgentConfigImpl.IBM_WORKAROUND_DEFAULT);
        this.instrumentationConfig = new BaseConfig(this.nestedProps("instrumentation"), "newrelic.config.instrumentation");
        this.maxStackTraceLines = this.getProperty("max_stack_trace_lines", 30);
        final String[] jdbcSupport = this.getProperty("jdbc_support", "generic").split(",");
        this.jdbcSupport = new HashSet<String>(Arrays.asList(jdbcSupport));
        this.genericJdbcSupportEnabled = this.jdbcSupport.contains("generic");
        final Map<String, Object> propsWithSystemProps = (Map<String, Object>)Maps.newHashMap((Map<?, ?>)props);
        propsWithSystemProps.putAll(SystemPropertyFactory.getSystemPropertyProvider().getNewRelicPropertiesWithoutPrefix());
        final Map<String, Object> flattenedProps = (Map<String, Object>)Maps.newHashMap();
        this.flatten("", propsWithSystemProps, flattenedProps);
        this.checkHighSecurityPropsInFlattened(flattenedProps);
        this.flattenedProperties = Collections.unmodifiableMap((Map<? extends String, ?>)flattenedProps);
    }
    
    private void checkHighSecurityPropsInFlattened(final Map<String, Object> flattenedProps) {
        if (this.highSecurity && !flattenedProps.isEmpty()) {
            flattenedProps.put("ssl", this.isSSL);
            flattenedProps.put("transaction_tracer.record_sql", this.transactionTracerConfig.getRecordSql());
        }
    }
    
    private boolean initSsl(final boolean isHighSec, final Map<String, Object> props) {
        boolean ssl;
        if (isHighSec) {
            ssl = true;
            props.put("ssl", Boolean.TRUE);
        }
        else {
            ssl = this.getProperty("ssl", true);
        }
        return ssl;
    }
    
    private void flatten(final String prefix, final Map<String, Object> source, final Map<String, Object> dest) {
        for (final Map.Entry<String, Object> e : source.entrySet()) {
            if (e.getValue() instanceof Map) {
                this.flatten(prefix + e.getKey() + '.', e.getValue(), dest);
            }
            else {
                dest.put(prefix + e.getKey(), e.getValue());
            }
        }
    }
    
    public <T> T getValue(final String path) {
        return this.getValue(path, (T)null);
    }
    
    public <T> T getValue(final String path, final T defaultValue) {
        Object value = this.flattenedProperties.get(path);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof ServerProp) {
            value = ((ServerProp)value).getValue();
            return this.castValue(path, value, defaultValue);
        }
        if (value instanceof String && defaultValue instanceof Boolean) {
            value = Boolean.valueOf((String)value);
            return (T)value;
        }
        if (value instanceof String && defaultValue instanceof Integer) {
            value = Integer.valueOf((String)value);
            return (T)value;
        }
        try {
            return (T)value;
        }
        catch (ClassCastException ccx) {
            Agent.LOG.log(Level.FINE, "Using default value \"{0}\" for \"{1}\"", new Object[] { defaultValue, path });
            return defaultValue;
        }
    }
    
    private String initLogLevel() {
        final Object val = this.getProperty("log_level", "info");
        if (val instanceof Boolean) {
            return "off";
        }
        return this.getProperty("log_level", "info").toLowerCase();
    }
    
    private CrossProcessConfig initCrossProcessConfig() {
        final Boolean prop = this.getProperty("cross_application_tracing");
        Map<String, Object> props = this.nestedProps("cross_application_tracer");
        if (prop != null) {
            if (props == null) {
                props = this.createMap();
            }
            props.put("cross_application_tracing", prop);
        }
        return CrossProcessConfigImpl.createCrossProcessConfig(props);
    }
    
    private StripExceptionConfig initStripExceptionConfig(final boolean highSecurity) {
        final Map<String, Object> props = this.nestedProps("strip_exception_messages");
        return StripExceptionConfigImpl.createStripExceptionConfig(props, highSecurity);
    }
    
    private ThreadProfilerConfig initThreadProfilerConfig() {
        final Map<String, Object> props = this.nestedProps("thread_profiler");
        return ThreadProfilerConfigImpl.createThreadProfilerConfig(props);
    }
    
    private KeyTransactionConfig initKeyTransactionConfig(final long apdexTInMillis) {
        final Map<String, Object> props = this.nestedProps("web_transactions_apdex");
        return KeyTransactionConfigImpl.createKeyTransactionConfig(props, apdexTInMillis);
    }
    
    private TransactionTracerConfigImpl initTransactionTracerConfig(final long apdexTInMillis, final boolean highSecurity) {
        final Map<String, Object> props = this.nestedProps("transaction_tracer");
        return TransactionTracerConfigImpl.createTransactionTracerConfig(props, apdexTInMillis, highSecurity);
    }
    
    private ErrorCollectorConfig initErrorCollectorConfig() {
        final Map<String, Object> props = this.nestedProps("error_collector");
        return ErrorCollectorConfigImpl.createErrorCollectorConfig(props);
    }
    
    private SqlTraceConfig initSqlTraceConfig() {
        final Map<String, Object> props = this.nestedProps("slow_sql");
        return SqlTraceConfigImpl.createSqlTraceConfig(props);
    }
    
    private JmxConfig initJmxConfig() {
        final Map<String, Object> props = this.nestedProps("jmx");
        return JmxConfigImpl.createJmxConfig(props);
    }
    
    private JarCollectorConfig initJarCollectorConfig() {
        final Map<String, Object> props = this.nestedProps("jar_collector");
        return JarCollectorConfigImpl.createJarCollectorConfig(props);
    }
    
    private ReinstrumentConfig initReinstrumentConfig() {
        final Map<String, Object> props = this.nestedProps("reinstrument");
        return ReinstrumentConfigImpl.createReinstrumentConfig(props);
    }
    
    private BrowserMonitoringConfig initBrowserMonitoringConfig() {
        final Map<String, Object> props = this.nestedProps("browser_monitoring");
        return BrowserMonitoringConfigImpl.createBrowserMonitoringConfig(props);
    }
    
    private ClassTransformerConfig initClassTransformerConfig() {
        final boolean customTracingEnabled = this.getProperty("enable_custom_tracing", true);
        final Map<String, Object> props = this.nestedProps("class_transformer");
        return ClassTransformerConfigImpl.createClassTransformerConfig(props, customTracingEnabled);
    }
    
    private CircuitBreakerConfig initCircuitBreakerConfig() {
        final Map<String, Object> props = this.nestedProps("circuitbreaker");
        return new CircuitBreakerConfig(props);
    }
    
    public long getApdexTInMillis() {
        return this.apdexTInMillis;
    }
    
    public long getApdexTInMillis(final String transactionName) {
        return this.keyTransactionConfig.getApdexTInMillis(transactionName);
    }
    
    public boolean isApdexTSet() {
        return this.isApdexTSet;
    }
    
    public boolean isApdexTSet(final String transactionName) {
        return this.keyTransactionConfig.isApdexTSet(transactionName);
    }
    
    public boolean isAgentEnabled() {
        return this.enabled;
    }
    
    public String getLicenseKey() {
        return this.licenseKey;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getProxyHost() {
        return this.proxyHost;
    }
    
    public Integer getProxyPort() {
        return this.proxyPort;
    }
    
    public String getProxyUser() {
        return this.proxyUser;
    }
    
    public String getProxyPassword() {
        return this.proxyPass;
    }
    
    public String getApiHost() {
        return this.getProperty("api_host", "rpm.newrelic.com");
    }
    
    public int getApiPort() {
        return this.getProperty("api_port", this.isSSL ? 443 : 80);
    }
    
    public boolean isSSL() {
        return this.isSSL;
    }
    
    public String getApplicationName() {
        return this.appName;
    }
    
    public List<String> getApplicationNames() {
        return this.appNames;
    }
    
    public boolean isCpuSamplingEnabled() {
        return this.cpuSamplingEnabled;
    }
    
    public boolean isAutoAppNamingEnabled() {
        return this.autoAppNamingEnabled;
    }
    
    public boolean isAutoTransactionNamingEnabled() {
        return this.autoTransactionNamingEnabled;
    }
    
    public boolean isDebugEnabled() {
        return this.debug;
    }
    
    public boolean isSessionCountTrackingEnabled() {
        return this.sessionCountTrackingEnabled;
    }
    
    public String getLanguage() {
        return this.getProperty("language", "java");
    }
    
    public boolean isSendDataOnExit() {
        return this.getProperty("send_data_on_exit", false);
    }
    
    public long getSendDataOnExitThresholdInMillis() {
        final int valueInSecs = this.getIntProperty("send_data_on_exit_threshold", 60);
        return TimeUnit.MILLISECONDS.convert(valueInSecs, TimeUnit.SECONDS);
    }
    
    public boolean isAuditMode() {
        return this.auditMode;
    }
    
    public boolean isReportSqlParserErrors() {
        return this.reportSqlParserErrors;
    }
    
    public int getTransactionSizeLimit() {
        return this.transactionSizeLimit;
    }
    
    public boolean waitForRPMConnect() {
        return this.waitForRPMConnect;
    }
    
    public boolean isSyncStartup() {
        return this.getProperty("sync_startup", false);
    }
    
    public boolean isSendEnvironmentInfo() {
        return this.getProperty("send_environment_info", true);
    }
    
    public boolean isLoggingToStdOut() {
        final String logFileName = this.getLogFileName();
        return "STDOUT".equalsIgnoreCase(logFileName);
    }
    
    public int getLogFileCount() {
        return this.getIntProperty("log_file_count", 1);
    }
    
    public String getLogFileName() {
        return this.getProperty("log_file_name", "newrelic_agent.log");
    }
    
    public String getLogFilePath() {
        return this.getProperty("log_file_path");
    }
    
    public String getLogLevel() {
        return this.logLevel;
    }
    
    public int getLogLimit() {
        return this.getIntProperty("log_limit_in_kbytes", 0);
    }
    
    public TransactionTracerConfig getTransactionTracerConfig() {
        return this.transactionTracerConfig;
    }
    
    public TransactionTracerConfig getBackgroundTransactionTracerConfig() {
        return this.backgroundTransactionTracerConfig;
    }
    
    public TransactionTracerConfig getRequestTransactionTracerConfig() {
        return this.requestTransactionTracerConfig;
    }
    
    public ErrorCollectorConfig getErrorCollectorConfig() {
        return this.errorCollectorConfig;
    }
    
    public SqlTraceConfig getSqlTraceConfig() {
        return this.sqlTraceConfig;
    }
    
    public CrossProcessConfig getCrossProcessConfig() {
        return this.crossProcessConfig;
    }
    
    public ThreadProfilerConfig getThreadProfilerConfig() {
        return this.threadProfilerConfig;
    }
    
    public JmxConfig getJmxConfig() {
        return this.jmxConfig;
    }
    
    public JarCollectorConfig getJarCollectorConfig() {
        return this.jarCollectorConfig;
    }
    
    public ReinstrumentConfig getReinstrumentConfig() {
        return this.reinstrumentConfig;
    }
    
    public BrowserMonitoringConfig getBrowserMonitoringConfig() {
        return this.browserMonitoringConfig;
    }
    
    public ClassTransformerConfig getClassTransformerConfig() {
        return this.classTransformerConfig;
    }
    
    public List<String> getIgnoreJars() {
        return this.ignoreJars;
    }
    
    public boolean isSendJvmProps() {
        return this.sendJvmProps;
    }
    
    public boolean isUsePrivateSSL() {
        return this.usePrivateSSL;
    }
    
    public boolean isLogDaily() {
        return this.logDaily;
    }
    
    public boolean isXraySessionEnabled() {
        return this.xRaySessionsEnabled;
    }
    
    public boolean isTrimStats() {
        return this.trimStats;
    }
    
    public static AgentConfig createAgentConfig(Map<String, Object> settings) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new AgentConfigImpl(settings);
    }
    
    public boolean isPlatformInformationEnabled() {
        return this.platformInformationEnabled;
    }
    
    public Set<String> getJDBCSupport() {
        return this.jdbcSupport;
    }
    
    public boolean isGenericJDBCSupportEnabled() {
        return this.genericJdbcSupportEnabled;
    }
    
    public int getMaxStackTraceLines() {
        return this.maxStackTraceLines;
    }
    
    public Config getInstrumentationConfig() {
        return this.instrumentationConfig;
    }
    
    public int getMaxUserParameters() {
        return 64;
    }
    
    public int getMaxUserParameterSize() {
        return 255;
    }
    
    public boolean isHighSecurity() {
        return this.highSecurity;
    }
    
    public boolean getIbmWorkaroundEnabled() {
        return this.ibmWorkaroundEnabled;
    }
    
    public LabelsConfig getLabelsConfig() {
        return this.labelsConfig;
    }
    
    public boolean isStartupTimingEnabled() {
        return this.startupTimingEnabled;
    }
    
    public CircuitBreakerConfig getCircuitBreakerConfig() {
        return this.circuitBreakerConfig;
    }
    
    public StripExceptionConfig getStripExceptionConfig() {
        return this.stripExceptionConfig;
    }
    
    static {
        DEFAULT_PROXY_HOST = null;
        IBM_WORKAROUND_DEFAULT = IBMUtils.getIbmWorkaroundDefault();
    }
}

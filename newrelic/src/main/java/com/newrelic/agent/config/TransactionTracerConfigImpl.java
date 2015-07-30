// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;

public final class TransactionTracerConfigImpl extends BaseConfig implements TransactionTracerConfig
{
    public static final String BACKGROUND_CATEGORY_NAME = "background";
    public static final String REQUEST_CATEGORY_NAME = "request";
    public static final String APDEX_F = "apdex_f";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_NAME = "name";
    public static final String COLLECT_TRACES = "collect_traces";
    public static final String ENABLED = "enabled";
    public static final String EXPLAIN_ENABLED = "explain_enabled";
    public static final String EXPLAIN_THRESHOLD = "explain_threshold";
    public static final String GC_TIME_ENABLED = "gc_time_enabled";
    public static final String INSERT_SQL_MAX_LENGTH = "insert_sql_max_length";
    public static final String LOG_SQL = "log_sql";
    public static final String MAX_EXPLAIN_PLANS = "max_explain_plans";
    public static final String MAX_STACK_TRACE = "max_stack_trace";
    public static final String OBFUSCATED_SQL_FIELDS = "obfuscated_sql_fields";
    public static final String RECORD_SQL = "record_sql";
    public static final String SEGMENT_LIMIT = "segment_limit";
    public static final String STACK_TRACE_THRESHOLD = "stack_trace_threshold";
    public static final String TOP_N = "top_n";
    public static final String TRANSACTION_THRESHOLD = "transaction_threshold";
    public static final String TAKE_LAST_STATUS = "take_last_status";
    public static final String STACK_BASED_NAMING = "stack_based_naming";
    public static final boolean DEFAULT_STACK_BASED_NAMING = false;
    public static final boolean DEFAULT_COLLECT_TRACES = false;
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_EXPLAIN_ENABLED = true;
    public static final boolean DEFAULT_TAKE_LAST_STATUS = false;
    public static final double DEFAULT_EXPLAIN_THRESHOLD = 0.5;
    public static final boolean DEFAULT_GC_TIME_ENABLED = false;
    public static final int DEFAULT_INSERT_SQL_MAX_LENGTH = 2000;
    public static final boolean DEFAULT_LOG_SQL = false;
    public static final int DEFAULT_MAX_EXPLAIN_PLANS = 20;
    public static final int DEFAULT_MAX_STACK_TRACE = 20;
    public static final String DEFAULT_RECORD_SQL = "obfuscated";
    public static final int DEFAULT_SEGMENT_LIMIT = 3000;
    public static final double DEFAULT_STACK_TRACE_THRESHOLD = 0.5;
    public static final String DEFAULT_TRANSACTION_THRESHOLD = "apdex_f";
    public static final int DEFAULT_TOP_N = 20;
    public static final int APDEX_F_MULTIPLE = 4;
    public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.transaction_tracer.";
    public static final String CATEGORY_REQUEST_SYSTEM_PROPERTY_ROOT = "newrelic.config.transaction_tracer.category.request.";
    public static final String CATEGORY_BACKGROUND_SYSTEM_PROPERTY_ROOT = "newrelic.config.transaction_tracer.category.background.";
    private final boolean isEnabled;
    private final boolean isExplainEnabled;
    private final boolean isLogSql;
    private final boolean takeLastStatus;
    private final String recordSql;
    private final double explainThreshold;
    private final double explainThresholdInNanos;
    private final double stackTraceThreshold;
    private final double stackTraceThresholdInNanos;
    private final long transactionThreshold;
    private final long transactionThresholdInNanos;
    private final int insertSqlMaxLength;
    private final boolean gcTimeEnabled;
    private final int maxStackTraces;
    private final int maxSegments;
    private final int maxExplainPlans;
    private final int topN;
    protected final String inheritedFromSystemPropertyRoot;
    private final boolean stackBasedNamingEnabled;
    
    private TransactionTracerConfigImpl(final String systemPropertyRoot, final String inheritedFromSystemPropertyRoot, final Map<String, Object> props, final long apdexTInMillis, final boolean highSecurity) {
        super(props, systemPropertyRoot);
        this.inheritedFromSystemPropertyRoot = inheritedFromSystemPropertyRoot;
        this.isEnabled = this.initEnabled();
        this.isExplainEnabled = this.getProperty("explain_enabled", true);
        this.isLogSql = this.getProperty("log_sql", false);
        this.takeLastStatus = this.getProperty("take_last_status", false);
        if (this.takeLastStatus) {
            Agent.LOG.log(Level.INFO, MessageFormat.format("The property {0} has been deprecated.", "take_last_status"));
        }
        this.recordSql = this.initRecordSql(highSecurity, props).intern();
        this.explainThreshold = this.getDoubleProperty("explain_threshold", 0.5) * 1000.0;
        this.explainThresholdInNanos = TimeUnit.NANOSECONDS.convert((long)this.explainThreshold, TimeUnit.MILLISECONDS);
        this.stackTraceThreshold = this.getDoubleProperty("stack_trace_threshold", 0.5) * 1000.0;
        this.stackTraceThresholdInNanos = TimeUnit.NANOSECONDS.convert((long)this.stackTraceThreshold, TimeUnit.MILLISECONDS);
        this.transactionThreshold = this.initTransactionThreshold(apdexTInMillis);
        this.transactionThresholdInNanos = TimeUnit.NANOSECONDS.convert(this.transactionThreshold, TimeUnit.MILLISECONDS);
        this.insertSqlMaxLength = this.getIntProperty("insert_sql_max_length", 2000);
        this.gcTimeEnabled = this.getProperty("gc_time_enabled", false);
        this.maxStackTraces = this.getIntProperty("max_stack_trace", 20);
        this.maxSegments = this.getIntProperty("segment_limit", 3000);
        this.maxExplainPlans = this.getIntProperty("max_explain_plans", 20);
        this.topN = this.getIntProperty("top_n", 20);
        this.stackBasedNamingEnabled = this.getProperty("stack_based_naming", false);
    }
    
    private boolean initEnabled() {
        final boolean isEnabled = this.getProperty("enabled", true);
        final boolean canCollectTraces = this.getProperty("collect_traces", false);
        return isEnabled && canCollectTraces;
    }
    
    protected String initRecordSql(final boolean highSecurity, final Map<String, Object> props) {
        final Object val = this.getProperty("record_sql", "obfuscated");
        String output;
        if (val instanceof Boolean) {
            output = "off";
        }
        else {
            output = this.getProperty("record_sql", "obfuscated").toLowerCase();
            if (!this.getUniqueStrings("obfuscated_sql_fields").isEmpty()) {
                Agent.LOG.log(Level.WARNING, "The {0} setting is no longer supported.  Full SQL obfuscation is enabled.", new Object[] { "obfuscated_sql_fields" });
                output = "obfuscated";
            }
        }
        if (highSecurity && !"off".equals(output)) {
            output = "obfuscated";
        }
        return output;
    }
    
    private long initTransactionThreshold(final long apdexTInMillis) {
        final Object threshold = this.getProperty("transaction_threshold", "apdex_f");
        if ("apdex_f".equals(threshold)) {
            return apdexTInMillis * 4L;
        }
        final Number transactionThreshold = this.getProperty("transaction_threshold");
        return (long)(transactionThreshold.doubleValue() * 1000.0);
    }
    
    private Map<String, Object> initCategorySettings(final String category) {
        final Set<Map<String, Object>> categories = this.getMapSet("category");
        for (final Map<String, Object> categoryProps : categories) {
            if (category.equals(categoryProps.get("name"))) {
                return this.mergeSettings(this.getProperties(), categoryProps);
            }
        }
        return this.getProperties();
    }
    
    private Map<String, Object> mergeSettings(final Map<String, Object> localSettings, final Map<String, Object> serverSettings) {
        final Map<String, Object> mergedSettings = this.createMap();
        if (localSettings != null) {
            mergedSettings.putAll(localSettings);
        }
        if (serverSettings != null) {
            mergedSettings.putAll(serverSettings);
        }
        return mergedSettings;
    }
    
    protected String getInheritedSystemPropertyKey(final String key) {
        return (this.inheritedFromSystemPropertyRoot == null) ? null : (this.inheritedFromSystemPropertyRoot + key);
    }
    
    protected Object getPropertyFromSystemProperties(final String name, final Object defaultVal) {
        final String key = this.getSystemPropertyKey(name);
        final Object value = BaseConfig.parseValue(SystemPropertyFactory.getSystemPropertyProvider().getSystemProperty(key));
        if (value != null) {
            return value;
        }
        final String inheritedKey = this.getInheritedSystemPropertyKey(name);
        return (inheritedKey == null) ? null : BaseConfig.parseValue(SystemPropertyFactory.getSystemPropertyProvider().getSystemProperty(inheritedKey));
    }
    
    protected Object getPropertyFromSystemEnvironment(final String name, final Object defaultVal) {
        final String key = this.getSystemPropertyKey(name);
        final Object value = BaseConfig.parseValue(SystemPropertyFactory.getSystemPropertyProvider().getEnvironmentVariable(key));
        if (value != null) {
            return value;
        }
        final String inheritedKey = this.getInheritedSystemPropertyKey(name);
        return (inheritedKey == null) ? null : BaseConfig.parseValue(SystemPropertyFactory.getSystemPropertyProvider().getEnvironmentVariable(inheritedKey));
    }
    
    public double getExplainThresholdInMillis() {
        return this.explainThreshold;
    }
    
    public double getExplainThresholdInNanos() {
        return this.explainThresholdInNanos;
    }
    
    public String getRecordSql() {
        return this.recordSql;
    }
    
    public double getStackTraceThresholdInMillis() {
        return this.stackTraceThreshold;
    }
    
    public double getStackTraceThresholdInNanos() {
        return this.stackTraceThresholdInNanos;
    }
    
    public long getTransactionThresholdInMillis() {
        return this.transactionThreshold;
    }
    
    public long getTransactionThresholdInNanos() {
        return this.transactionThresholdInNanos;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public boolean isExplainEnabled() {
        return this.isExplainEnabled;
    }
    
    public int getMaxExplainPlans() {
        return this.maxExplainPlans;
    }
    
    public int getTopN() {
        return this.topN;
    }
    
    public boolean isLogSql() {
        return this.isLogSql;
    }
    
    public boolean isGCTimeEnabled() {
        return this.gcTimeEnabled;
    }
    
    public int getInsertSqlMaxLength() {
        return this.insertSqlMaxLength;
    }
    
    public int getMaxStackTraces() {
        return this.maxStackTraces;
    }
    
    public int getMaxSegments() {
        return this.maxSegments;
    }
    
    public boolean isStackBasedNamingEnabled() {
        return this.stackBasedNamingEnabled;
    }
    
    TransactionTracerConfigImpl createRequestTransactionTracerConfig(final long apdexTInMillis, final boolean highSecurity) {
        final Map<String, Object> settings = this.initCategorySettings("request");
        return new TransactionTracerConfigImpl("newrelic.config.transaction_tracer.category.request.", "newrelic.config.transaction_tracer.", settings, apdexTInMillis, highSecurity);
    }
    
    TransactionTracerConfigImpl createBackgroundTransactionTracerConfig(final long apdexTInMillis, final boolean highSecurity) {
        final Map<String, Object> settings = this.initCategorySettings("background");
        return new TransactionTracerConfigImpl("newrelic.config.transaction_tracer.category.background.", "newrelic.config.transaction_tracer.", settings, apdexTInMillis, highSecurity);
    }
    
    static TransactionTracerConfigImpl createTransactionTracerConfig(final Map<String, Object> settings, final long apdexTInMillis, final boolean highSecurity) {
        return createTransactionTracerConfigImpl(settings, apdexTInMillis, highSecurity);
    }
    
    private static TransactionTracerConfigImpl createTransactionTracerConfigImpl(Map<String, Object> settings, final long apdexTInMillis, final boolean highSecurity) {
        if (settings == null) {
            settings = Collections.emptyMap();
        }
        return new TransactionTracerConfigImpl("newrelic.config.transaction_tracer.", null, settings, apdexTInMillis, highSecurity);
    }
}

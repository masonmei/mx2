// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.HashMap;
import java.util.Iterator;
import com.newrelic.agent.transport.DataSenderWriter;
import java.util.Map;

public class AgentConfigFactory
{
    public static final String AGENT_CONFIG = "agent_config";
    public static final String PERIOD_REGEX = "\\.";
    public static final String DOT_SEPARATOR = ".";
    public static final String SLOW_SQL_PREFIX = "slow_sql.";
    public static final String TRANSACTION_TRACER_PREFIX = "transaction_tracer.";
    public static final String TRANSACTION_TRACER_CATEGORY_BACKGROUND_PREFIX = "transaction_tracer.category.background.";
    public static final String TRANSACTION_TRACER_CATEGORY_REQUEST_PREFIX = "transaction_tracer.category.request.";
    public static final String ERROR_COLLECTOR_PREFIX = "error_collector.";
    public static final String THREAD_PROFILER_PREFIX = "thread_profiler.";
    public static final String TRANSACTION_EVENTS_PREFIX = "transaction_events.";
    public static final String CUSTOM_INSIGHT_EVENTS_PREFIX = "custom_insights_events.";
    public static final String BROSWER_MONITORING_PREFIX = "browser_monitoring.";
    public static final String HIGH_SECURITY = "high_security";
    public static final String COLLECT_ERRORS = "error_collector.collect_errors";
    public static final String COLLECT_TRACES = "transaction_tracer.collect_traces";
    public static final String COLLECT_TRANSACTION_EVENTS = "transaction_events.collect_analytics_events";
    public static final String COLLECT_CUSTOM_INSIGHTS_EVENTS = "custom_insights_events.collect_custom_events";
    public static final String RECORD_SQL = "transaction_tracer.record_sql";
    public static final String CROSS_APPLICATION_TRACER_PREFIX = "cross_application_tracer.";
    public static final String ENCODING_KEY = "cross_application_tracer.encoding_key";
    public static final String CROSS_PROCESS_ID = "cross_application_tracer.cross_process_id";
    public static final String TRUSTED_ACCOUNT_IDS = "cross_application_tracer.trusted_account_ids";
    public static final String STRIP_EXCEPTION = "strip_exception_messages";
    public static final String STRIP_EXCEPTION_ENABLED = "strip_exception_messages.enabled";
    public static final String STRIP_EXCEPTION_WHITELIST = "strip_exception_messages.whitelist";
    
    public static AgentConfig createAgentConfig(final Map<String, Object> localSettings, final Map<String, Object> serverData) {
        final Map<String, Object> mergedSettings = createMap(localSettings);
        mergeServerData(mergedSettings, serverData);
        return AgentConfigImpl.createAgentConfig(mergedSettings);
    }
    
    public static Map<String, Object> getAgentData(final Map<String, Object> serverData) {
        if (serverData == null) {
            return createMap();
        }
        final Object agentData = serverData.get("agent_config");
        if (agentData == null || DataSenderWriter.nullValue().equals(agentData)) {
            return createMap();
        }
        return (Map<String, Object>)agentData;
    }
    
    public static void mergeServerData(final Map<String, Object> settings, final Map<String, Object> serverData) {
        if (serverData == null) {
            return;
        }
        final Map<String, Object> agentData = getAgentData(serverData);
        agentData.remove("cross_application_tracing");
        agentData.remove("high_security");
        agentData.remove("reinstrument");
        agentData.remove("reinstrument.attributes_enabled");
        agentData.remove("strip_exception_messages");
        agentData.remove("strip_exception_messages.enabled");
        agentData.remove("strip_exception_messages.whitelist");
        if (isHighSecurity(settings.get("high_security"))) {
            removeInvalidHighSecuritySettings(agentData, serverData);
            if (isValidRecordSqlValue(serverData.get("record_sql"))) {
                addServerProp("transaction_tracer.record_sql", serverData.get("record_sql"), settings);
            }
        }
        else {
            addServerProp("transaction_tracer.record_sql", serverData.get("record_sql"), settings);
        }
        mergeAgentData(settings, agentData);
        addServerProp("apdex_t", serverData.get("apdex_t"), settings);
        addServerProp("error_collector.collect_errors", serverData.get("collect_errors"), settings);
        addServerProp("transaction_tracer.collect_traces", serverData.get("collect_traces"), settings);
        addServerProp("transaction_events.collect_analytics_events", serverData.get("collect_analytics_events"), settings);
        addServerProp("custom_insights_events.collect_custom_events", serverData.get("collect_custom_events"), settings);
        addServerProp("web_transactions_apdex", serverData.get("web_transactions_apdex"), settings);
        addServerProp("cross_application_tracer.cross_process_id", serverData.get("cross_process_id"), settings);
        addServerProp("cross_application_tracer.encoding_key", serverData.get("encoding_key"), settings);
        addServerProp("cross_application_tracer.trusted_account_ids", serverData.get("trusted_account_ids"), settings);
        addServerProp("capture_params", serverData.get("capture_params"), settings);
    }
    
    private static void removeInvalidHighSecuritySettings(final Map<String, Object> agentData, final Map<String, Object> serverData) {
        agentData.remove("ssl");
        final Object recordSql = agentData.get("transaction_tracer.record_sql");
        if (!isValidRecordSqlValue(recordSql)) {
            agentData.remove("transaction_tracer.record_sql");
        }
    }
    
    private static boolean isValidRecordSqlValue(final Object recordSqlValue) {
        if (recordSqlValue == null || !(recordSqlValue instanceof String)) {
            return false;
        }
        final String rSql = ((String)recordSqlValue).toLowerCase();
        return rSql.equals("off") || rSql.equals("obfuscated");
    }
    
    private static boolean isHighSecurity(final Object value) {
        return value != null && value instanceof Boolean && (Boolean)value;
    }
    
    private static void mergeAgentData(final Map<String, Object> settings, final Map<String, Object> agentData) {
        for (final Map.Entry<String, Object> entry : agentData.entrySet()) {
            addServerProp(entry.getKey(), entry.getValue(), settings);
        }
    }
    
    private static void addServerProp(final String prop, final Object val, final Map<String, Object> settings) {
        if (val == null) {
            return;
        }
        Map<String, Object> currentMap = settings;
        int count = 0;
        final String[] arr$;
        final String[] propArray = arr$ = prop.split("\\.");
        for (final String propPart : arr$) {
            if (++count < propArray.length) {
                Map<String, Object> propMap = (Map<String, Object>) currentMap.get(propPart);
                if (propMap == null) {
                    propMap = createMap();
                    currentMap.put(propPart, propMap);
                }
                currentMap = propMap;
            }
            else {
                currentMap.put(propPart, ServerProp.createPropObject(val));
            }
        }
    }
    
    private static Map<String, Object> createMap() {
        return new HashMap<String, Object>();
    }
    
    public static Map<String, Object> createMap(final Map<String, Object> settings) {
        final Map<String, Object> result = createMap();
        if (settings == null) {
            return result;
        }
        for (final Map.Entry<String, Object> entry : settings.entrySet()) {
            putOrCreate(entry.getKey(), entry.getValue(), result);
        }
        return result;
    }
    
    private static void putOrCreate(final String key, final Object val, final Map<String, Object> settings) {
        if (val instanceof Map) {
            settings.put(key, createMap((Map<String, Object>)val));
        }
        else {
            settings.put(key, val);
        }
    }
}

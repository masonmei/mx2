// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

import java.util.Set;
import java.util.List;
import com.newrelic.api.agent.Config;

public interface AgentConfig extends Config
{
    boolean isAgentEnabled();
    
    String getLicenseKey();
    
    String getApplicationName();
    
    List<String> getApplicationNames();
    
    boolean isAutoAppNamingEnabled();
    
    boolean isAutoTransactionNamingEnabled();
    
    long getApdexTInMillis();
    
    long getApdexTInMillis(String p0);
    
    boolean isApdexTSet();
    
    boolean isApdexTSet(String p0);
    
    String getHost();
    
    int getTransactionSizeLimit();
    
    boolean isSyncStartup();
    
    boolean waitForRPMConnect();
    
    TransactionTracerConfig getTransactionTracerConfig();
    
    ClassTransformerConfig getClassTransformerConfig();
    
    BrowserMonitoringConfig getBrowserMonitoringConfig();
    
    TransactionTracerConfig getRequestTransactionTracerConfig();
    
    TransactionTracerConfig getBackgroundTransactionTracerConfig();
    
    ErrorCollectorConfig getErrorCollectorConfig();
    
    ThreadProfilerConfig getThreadProfilerConfig();
    
    SqlTraceConfig getSqlTraceConfig();
    
    JmxConfig getJmxConfig();
    
    JarCollectorConfig getJarCollectorConfig();
    
    ReinstrumentConfig getReinstrumentConfig();
    
    CrossProcessConfig getCrossProcessConfig();
    
    boolean isSessionCountTrackingEnabled();
    
    String getLanguage();
    
     <T> T getProperty(String p0);
    
     <T> T getProperty(String p0, T p1);
    
    int getPort();
    
    boolean isSSL();
    
    boolean isAuditMode();
    
    String getProxyHost();
    
    Integer getProxyPort();
    
    String getProxyPassword();
    
    String getProxyUser();
    
    boolean isSendEnvironmentInfo();
    
    String getApiHost();
    
    int getApiPort();
    
    boolean isDebugEnabled();
    
    boolean isReportSqlParserErrors();
    
    boolean isLoggingToStdOut();
    
    String getLogFileName();
    
    String getLogFilePath();
    
    String getLogLevel();
    
    List<String> getIgnoreJars();
    
    int getLogLimit();
    
    int getLogFileCount();
    
    boolean isLogDaily();
    
    boolean isSendDataOnExit();
    
    long getSendDataOnExitThresholdInMillis();
    
    boolean isCpuSamplingEnabled();
    
    boolean isSendJvmProps();
    
    boolean isUsePrivateSSL();
    
    boolean isXraySessionEnabled();
    
    boolean isTrimStats();
    
    boolean isPlatformInformationEnabled();
    
    Set<String> getJDBCSupport();
    
    boolean isGenericJDBCSupportEnabled();
    
    int getMaxStackTraceLines();
    
    com.newrelic.agent.config.Config getInstrumentationConfig();
    
    int getMaxUserParameters();
    
    int getMaxUserParameterSize();
    
    boolean isHighSecurity();
    
    boolean getIbmWorkaroundEnabled();
    
    LabelsConfig getLabelsConfig();
    
    boolean isStartupTimingEnabled();
    
    CircuitBreakerConfig getCircuitBreakerConfig();
    
    StripExceptionConfig getStripExceptionConfig();
}

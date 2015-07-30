// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import java.io.File;
import java.io.IOException;
import com.newrelic.agent.config.AgentConfig;
import java.util.Iterator;
import java.util.Map;
import java.text.MessageFormat;
import com.newrelic.agent.config.AgentJarHelper;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;

class LogbackLogManager implements IAgentLogManager
{
    private static final String CONFIG_FILE_PROP = "logback.configurationFile";
    private static final String CONTEXT_SELECT_PROP = "logback.ContextSelector";
    private static final String STATUS_LIST_PROP = "logback.statusListenerClass";
    private final LogbackLogger rootLogger;
    private volatile String logFilePath;
    
    private LogbackLogManager(final String name) {
        this.rootLogger = this.initializeRootLogger(name);
    }
    
    private LogbackLogger createRootLogger(final String name) {
        final LogbackLogger logger = LogbackLogger.create(name, true);
        final String logLevel = this.getStartupLogLevel();
        logger.setLevel(logLevel);
        logger.addConsoleAppender();
        return logger;
    }
    
    private String getStartupLogLevel() {
        final String propName = "newrelic.config.startup_log_level";
        final String logLevel = System.getProperty(propName);
        if (logLevel == null) {
            return Level.INFO.levelStr.toLowerCase();
        }
        return logLevel.toLowerCase();
    }
    
    private LogbackLogger initializeRootLogger(final String name) {
        LogbackLogger logger = null;
        final Map<String, String> systemProps = new HashMap<String, String>();
        try {
            final String jarFileName = AgentJarHelper.getAgentJarFileName();
            if (jarFileName == null) {
                logger = LogbackLogger.create(name, true);
            }
            else {
                this.clearAllLogbackSystemProperties(systemProps);
                System.setProperty("logback.configurationFile", jarFileName);
                try {
                    logger = this.createRootLogger(name);
                    System.getProperties().remove("logback.configurationFile");
                    this.applyOriginalSystemProperties(systemProps, logger);
                }
                finally {
                    System.getProperties().remove("logback.configurationFile");
                    this.applyOriginalSystemProperties(systemProps, logger);
                }
            }
        }
        catch (Exception e) {
            if (logger == null) {
                logger = this.createRootLogger(name);
            }
            final String msg = MessageFormat.format("Error setting logback.configurationFile property: {0}", e);
            logger.warning(msg);
        }
        return logger;
    }
    
    private void clearAllLogbackSystemProperties(final Map<String, String> storedSystemProps) {
        this.clearLogbackSystemProperty("logback.configurationFile", storedSystemProps);
        this.clearLogbackSystemProperty("logback.ContextSelector", storedSystemProps);
        this.clearLogbackSystemProperty("logback.statusListenerClass", storedSystemProps);
    }
    
    private void clearLogbackSystemProperty(final String prop, final Map<String, String> storedSystemProps) {
        final String old = System.clearProperty(prop);
        if (old != null) {
            storedSystemProps.put(prop, old);
        }
    }
    
    private void applyOriginalSystemProperties(final Map<String, String> storedSystemProps, final LogbackLogger logger) {
        for (final Map.Entry<String, String> currentProp : storedSystemProps.entrySet()) {
            try {
                System.setProperty(currentProp.getKey(), currentProp.getValue());
            }
            catch (Exception e) {
                final String msg = MessageFormat.format("Error setting logback property {0} back to {1}. Error: {2}", currentProp.getKey(), currentProp.getValue(), e);
                logger.warning(msg);
            }
        }
    }
    
    public IAgentLogger getRootLogger() {
        return this.rootLogger;
    }
    
    public String getLogFilePath() {
        return this.logFilePath;
    }
    
    public void configureLogger(final AgentConfig pAgentConfig) {
        this.configureLogLevel(pAgentConfig);
        this.configureConsoleHandler(pAgentConfig);
        this.configureFileHandler(pAgentConfig);
    }
    
    private void configureFileHandler(final AgentConfig agentConfig) {
        final String logFileName = this.getLogFileName(agentConfig);
        if (logFileName == null) {
            return;
        }
        try {
            this.configureFileHandler(logFileName, agentConfig);
            this.logFilePath = logFileName;
            final String msg = MessageFormat.format("Writing to New Relic log file: {0}", logFileName);
            this.rootLogger.info(msg);
            this.rootLogger.info(MessageFormat.format("JRE vendor {0} version {1}", System.getProperty("java.vendor"), System.getProperty("java.version")));
            this.rootLogger.info(MessageFormat.format("JVM vendor {0} {1} version {2}", System.getProperty("java.vm.vendor"), System.getProperty("java.vm.name"), System.getProperty("java.vm.version")));
            this.rootLogger.fine(MessageFormat.format("JVM runtime version {0}", System.getProperty("java.runtime.version")));
            this.rootLogger.info(MessageFormat.format("OS {0} version {1} arch {2}", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch")));
        }
        catch (IOException e) {
            final String msg2 = MessageFormat.format("Unable to configure newrelic log file: {0}", logFileName);
            this.rootLogger.error(msg2);
            this.addConsoleHandler();
        }
    }
    
    private String getLogFileName(final AgentConfig agentConfig) {
        final File logFile = LogFileHelper.getLogFile(agentConfig);
        return (logFile == null) ? null : logFile.getPath();
    }
    
    private void configureLogLevel(final AgentConfig agentConfig) {
        if (agentConfig.isDebugEnabled()) {
            this.rootLogger.setLevel(Level.TRACE.levelStr.toLowerCase());
        }
        else {
            this.rootLogger.setLevel(agentConfig.getLogLevel());
        }
    }
    
    private void configureConsoleHandler(final AgentConfig agentConfig) {
        if (agentConfig.isDebugEnabled() || agentConfig.isLoggingToStdOut()) {
            this.addConsoleHandler();
        }
        else {
            this.rootLogger.removeConsoleAppender();
        }
    }
    
    private String configureFileHandler(final String logFileName, final AgentConfig agentConfig) throws IOException {
        this.rootLogger.addConsoleAppender();
        if (this.canWriteLogFile(logFileName)) {
            this.rootLogger.info(MessageFormat.format("New Relic Agent: Writing to log file: {0}", logFileName));
        }
        else {
            this.rootLogger.warning(MessageFormat.format("New Relic Agent: Unable to write log file: {0}. Please check permissions on the file and directory.", logFileName));
        }
        this.rootLogger.removeConsoleAppender();
        final int limit = agentConfig.getLogLimit() * 1024;
        final int fileCount = Math.max(1, agentConfig.getLogFileCount());
        final boolean isDaily = agentConfig.isLogDaily();
        this.rootLogger.addFileAppender(logFileName, limit, fileCount, isDaily);
        return logFileName;
    }
    
    private boolean canWriteLogFile(final String logFileName) {
        try {
            final File logFile = new File(logFileName);
            if (!logFile.exists()) {
                if (null != logFile.getParentFile()) {
                    logFile.getParentFile().mkdirs();
                }
                logFile.createNewFile();
            }
            return logFile.canWrite();
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public void addConsoleHandler() {
        this.rootLogger.addConsoleAppender();
    }
    
    public void setLogLevel(final String pLevel) {
        this.rootLogger.setLevel(pLevel);
    }
    
    public String getLogLevel() {
        return this.rootLogger.getLevel();
    }
    
    public static LogbackLogManager create(final String name) {
        return new LogbackLogManager(name);
    }
}

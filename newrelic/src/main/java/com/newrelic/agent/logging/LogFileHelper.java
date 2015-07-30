// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.config.ConfigFileHelper;
import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import java.io.File;
import com.newrelic.agent.config.AgentConfig;

class LogFileHelper
{
    private static final String NEW_RELIC_LOG_FILE = "newrelic.logfile";
    private static final String LOGS_DIRECTORY = "logs";
    
    public static File getLogFile(final AgentConfig agentConfig) {
        if (agentConfig.isLoggingToStdOut()) {
            return null;
        }
        final File f = getLogFileFromProperty();
        if (f != null) {
            return f;
        }
        return getLogFileFromConfig(agentConfig);
    }
    
    private static File getLogFileFromProperty() {
        final String logFileName = System.getProperty("newrelic.logfile");
        if (logFileName == null) {
            return null;
        }
        final File f = new File(logFileName);
        try {
            f.createNewFile();
            return f;
        }
        catch (Exception e) {
            final String msg = MessageFormat.format("Unable to create log file {0}. Check permissions on the directory. - {1}", logFileName, e);
            Agent.LOG.warning(msg);
            return null;
        }
    }
    
    private static File getLogFileFromConfig(final AgentConfig agentConfig) {
        final String logFileName = agentConfig.getLogFileName();
        final File logsDirectory = getLogsDirectory(agentConfig);
        return new File(logsDirectory, logFileName);
    }
    
    private static File getLogsDirectory(final AgentConfig agentConfig) {
        File f = getLogsDirectoryFromConfig(agentConfig);
        if (f != null) {
            return f;
        }
        f = getNewRelicLogsDirectory();
        if (f != null) {
            return f;
        }
        f = new File("logs");
        if (f.exists()) {
            return f;
        }
        return new File(".");
    }
    
    private static File getLogsDirectoryFromConfig(final AgentConfig agentConfig) {
        final String logFilePath = agentConfig.getLogFilePath();
        if (logFilePath == null) {
            return null;
        }
        final File f = new File(logFilePath);
        if (f.exists()) {
            return f;
        }
        final String msg = MessageFormat.format("The log_file_path {0} specified in newrelic.yml does not exist", logFilePath);
        Agent.LOG.config(msg);
        return null;
    }
    
    private static File getNewRelicLogsDirectory() {
        final File nrDir = ConfigFileHelper.getNewRelicDirectory();
        if (nrDir != null) {
            final File logs = new File(nrDir, "logs");
            logs.mkdir();
            return logs;
        }
        return null;
    }
}

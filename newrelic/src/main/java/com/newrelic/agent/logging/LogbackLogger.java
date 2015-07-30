// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import com.newrelic.agent.Agent;
import java.text.MessageFormat;
import com.newrelic.agent.deps.ch.qos.logback.core.encoder.Encoder;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.TriggeringPolicy;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.RollingPolicy;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.RollingFileAppender;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.core.FileAppender;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.Filter;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.ConsoleAppender;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import com.newrelic.agent.deps.org.slf4j.LoggerFactory;
import com.newrelic.agent.deps.com.google.common.collect.Maps;
import java.util.Map;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;

class LogbackLogger implements IAgentLogger
{
    private static final boolean PRUDENT_VALUE = false;
    private static final int MIN_FILE_COUNT = 1;
    private static final String CONSOLE_APPENDER_NAME = "Console";
    private static final String FILE_APPENDER_NAME = "File";
    private static final boolean APPEND_TO_FILE = true;
    private static final String CONVERSION_PATTERN = "%d{\"MMM d, yyyy HH:mm:ss ZZZZ\"} [%pid %i] %logger %ml: %m%n";
    private static final String SYSTEM_OUT = "System.out";
    private final Logger logger;
    private Map<String, IAgentLogger> childLoggers;
    
    private LogbackLogger(final String name, final boolean isAgentRoot) {
        this.childLoggers = (Map<String, IAgentLogger>)Maps.newConcurrentMap();
        this.logger = (Logger)LoggerFactory.getLogger(name);
        if (isAgentRoot) {
            this.logger.setAdditive(false);
            FineFilter.getFineFilter().start();
        }
    }
    
    public void severe(final String pMessage) {
        this.logger.error(pMessage);
    }
    
    public void error(final String pMessage) {
        this.logger.error(pMessage);
    }
    
    public void warning(final String pMessage) {
        this.logger.warn(pMessage);
    }
    
    public void info(final String pMessage) {
        this.logger.info(pMessage);
    }
    
    public void config(final String pMessage) {
        this.logger.info(pMessage);
    }
    
    public void fine(final String pMessage) {
        this.logger.debug(LogbackMarkers.FINE_MARKER, pMessage);
    }
    
    public void finer(final String pMessage) {
        this.logger.debug(LogbackMarkers.FINER_MARKER, pMessage);
    }
    
    public void finest(final String pMessage) {
        this.logger.trace(LogbackMarkers.FINEST_MARKER, pMessage);
    }
    
    public void debug(final String pMessage) {
        this.logger.debug(pMessage);
    }
    
    public void trace(final String pMessage) {
        this.logger.trace(pMessage);
    }
    
    public boolean isFineEnabled() {
        return this.logger.isDebugEnabled() && FineFilter.getFineFilter().isEnabledFor(Level.FINE);
    }
    
    public boolean isFinerEnabled() {
        return this.logger.isDebugEnabled() && FineFilter.getFineFilter().isEnabledFor(Level.FINER);
    }
    
    public boolean isFinestEnabled() {
        return this.logger.isTraceEnabled();
    }
    
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }
    
    public boolean isLoggable(final Level pLevel) {
        final LogbackLevel level = LogbackLevel.getLevel(pLevel);
        return level != null && (this.logger.isEnabledFor(level.getLogbackLevel()) && FineFilter.getFineFilter().isEnabledFor(pLevel));
    }
    
    public void log(final Level pLevel, final String pMessage, final Throwable pThrowable) {
        if (this.isLoggable(pLevel)) {
            final LogbackLevel level = LogbackLevel.getLevel(pLevel);
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                public Void run() {
                    LogbackLogger.this.logger.log(level.getMarker(), Logger.FQCN, level.getLogbackLevel().toLocationAwareLoggerInteger(level.getLogbackLevel()), pMessage, null, pThrowable);
                    return null;
                }
            });
        }
    }
    
    public void log(final Level pLevel, final String pMessage) {
        final LogbackLevel level = LogbackLevel.getLevel(pLevel);
        this.logger.log(level.getMarker(), Logger.FQCN, level.getLogbackLevel().toLocationAwareLoggerInteger(level.getLogbackLevel()), pMessage, null, null);
    }
    
    public void log(final Level pLevel, final String pMessage, final Object[] pArgs, final Throwable pThorwable) {
        final LogbackLevel level = LogbackLevel.getLevel(pLevel);
        this.logger.log(level.getMarker(), Logger.FQCN, level.getLogbackLevel().toLocationAwareLoggerInteger(level.getLogbackLevel()), pMessage, pArgs, pThorwable);
    }
    
    public IAgentLogger getChildLogger(final Class<?> pClazz) {
        return this.getChildLogger(pClazz.getName());
    }
    
    public IAgentLogger getChildLogger(final String pFullName) {
        final IAgentLogger logger = create(pFullName, false);
        this.childLoggers.put(pFullName, logger);
        return logger;
    }
    
    public void setLevel(final String level) {
        final LogbackLevel newLevel = LogbackLevel.getLevel(level, LogbackLevel.INFO);
        this.logger.setLevel(newLevel.getLogbackLevel());
        FineFilter.getFineFilter().setLevel(newLevel.getJavaLevel());
    }
    
    public String getLevel() {
        if (this.logger.getLevel() == com.newrelic.agent.deps.ch.qos.logback.classic.Level.DEBUG) {
            return FineFilter.getFineFilter().getLevel().toString();
        }
        return this.logger.getLevel().toString();
    }
    
    public void removeConsoleAppender() {
        this.logger.detachAppender("Console");
    }
    
    public void addConsoleAppender() {
        if (this.logger.getAppender("Console") != null) {
            return;
        }
        final ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setName("Console");
        consoleAppender.setTarget("System.out");
        consoleAppender.setEncoder(this.getEncoder(this.logger.getLoggerContext()));
        consoleAppender.setContext(this.logger.getLoggerContext());
        consoleAppender.addFilter(FineFilter.getFineFilter());
        consoleAppender.start();
        this.logger.addAppender(consoleAppender);
    }
    
    public void addFileAppender(final String fileName, final long logLimit, final int fileCount, final boolean isDaily) throws IOException {
        if (this.logger.getAppender("File") != null) {
            return;
        }
        final FileAppender<ILoggingEvent> fileAppender = this.createFileAppender(fileCount, logLimit, fileName, isDaily);
        fileAppender.addFilter(FineFilter.getFineFilter());
        fileAppender.setEncoder(this.getEncoder(this.logger.getLoggerContext()));
        fileAppender.start();
        this.logger.addAppender(fileAppender);
    }
    
    private FileAppender<ILoggingEvent> createDailyAppender(final int fileCount, final String fileName) {
        final RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
        fileAppender.setContext(this.logger.getLoggerContext());
        fileAppender.setName("File");
        fileAppender.setFile(fileName);
        fileAppender.setAppend(true);
        fileAppender.setPrudent(false);
        final TimeBasedRollingPolicy<ILoggingEvent> timePolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        timePolicy.setFileNamePattern(fileName + ".%d{yyyy-MM-dd}");
        timePolicy.setContext(this.logger.getLoggerContext());
        timePolicy.setMaxHistory(fileCount);
        timePolicy.setParent(fileAppender);
        fileAppender.setRollingPolicy(timePolicy);
        timePolicy.start();
        return fileAppender;
    }
    
    private FileAppender<ILoggingEvent> createFileAppender(final int fileCount, final long logLimit, final String fileName, final boolean isDaily) {
        if (isDaily) {
            return this.createDailyAppender(fileCount, fileName);
        }
        if (fileCount <= 1) {
            final FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
            fileAppender.setName("File");
            fileAppender.setFile(fileName);
            fileAppender.setAppend(true);
            fileAppender.setPrudent(false);
            fileAppender.setContext(this.logger.getLoggerContext());
            return fileAppender;
        }
        final RollingFileAppender<ILoggingEvent> fileAppender2 = new RollingFileAppender<ILoggingEvent>();
        fileAppender2.setContext(this.logger.getLoggerContext());
        fileAppender2.setName("File");
        fileAppender2.setFile(fileName);
        fileAppender2.setAppend(true);
        fileAppender2.setPrudent(false);
        final FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setContext(this.logger.getLoggerContext());
        rollingPolicy.setParent(fileAppender2);
        rollingPolicy.setMinIndex(1);
        rollingPolicy.setMaxIndex(fileCount - 1);
        rollingPolicy.setFileNamePattern(fileName + ".%i");
        fileAppender2.setRollingPolicy(rollingPolicy);
        final TriggeringPolicy<ILoggingEvent> triggerPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>(String.valueOf(logLimit));
        fileAppender2.setTriggeringPolicy(triggerPolicy);
        triggerPolicy.start();
        rollingPolicy.start();
        return fileAppender2;
    }
    
    private Encoder<ILoggingEvent> getEncoder(final Context context) {
        final CustomPatternLogbackEncoder encoder = new CustomPatternLogbackEncoder("%d{\"MMM d, yyyy HH:mm:ss ZZZZ\"} [%pid %i] %logger %ml: %m%n");
        encoder.setContext(context);
        encoder.start();
        return encoder;
    }
    
    public static LogbackLogger create(final String name, final boolean isAgentRoot) {
        return new LogbackLogger(name, isAgentRoot);
    }
    
    public void log(final Level level, final String pattern, final Object... parts) {
        if (this.isLoggable(level)) {
            this.log(level, this.getMessage(pattern, parts));
        }
    }
    
    public void log(final Level level, final Throwable t, final String pattern, final Object... parts) {
        this.log(level, this.getMessage(pattern, parts), t);
    }
    
    private String getMessage(final String pattern, final Object... parts) {
        return (parts == null) ? pattern : MessageFormat.format(pattern, this.formatValues(parts));
    }
    
    private Object[] formatValues(final Object[] parts) {
        final Object[] strings = new Object[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            strings[i] = this.formatValue(parts[i]);
        }
        return strings;
    }
    
    private Object formatValue(final Object obj) {
        if (obj instanceof Class) {
            return ((Class)obj).getName();
        }
        if (obj instanceof Throwable) {
            return obj.toString();
        }
        return obj;
    }
    
    public void logToChild(final String childName, final Level level, final String pattern, final Object... parts) {
        if (this.isLoggable(level)) {
            IAgentLogger logger = this.childLoggers.get(childName);
            if (logger == null) {
                logger = Agent.LOG;
            }
            logger.log(level, pattern, parts);
        }
    }
}

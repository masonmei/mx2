// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic;

import com.newrelic.agent.deps.org.slf4j.LoggerFactory;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.Appender;
import com.newrelic.agent.deps.ch.qos.logback.core.encoder.Encoder;
import com.newrelic.agent.deps.ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ILoggingEvent;
import com.newrelic.agent.deps.ch.qos.logback.core.ConsoleAppender;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.InfoStatus;

public class BasicConfigurator
{
    static final BasicConfigurator hiddenSingleton;
    
    public static void configure(final LoggerContext lc) {
        final StatusManager sm = lc.getStatusManager();
        if (sm != null) {
            sm.add(new InfoStatus("Setting up default configuration.", lc));
        }
        final ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
        ca.setContext(lc);
        ca.setName("console");
        final PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(lc);
        pl.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        pl.start();
        ca.setEncoder(pl);
        ca.start();
        final Logger rootLogger = lc.getLogger("ROOT");
        rootLogger.addAppender(ca);
    }
    
    public static void configureDefaultContext() {
        final LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        configure(lc);
    }
    
    static {
        hiddenSingleton = new BasicConfigurator();
    }
}

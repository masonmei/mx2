// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.impl;

import com.newrelic.agent.deps.org.slf4j.ILoggerFactory;
import com.newrelic.agent.deps.ch.qos.logback.core.util.StatusPrinter;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import com.newrelic.agent.deps.org.slf4j.helpers.Util;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.ContextInitializer;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder
{
    public static String REQUESTED_API_VERSION;
    static final String NULL_CS_URL = "http://logback.qos.ch/codes.html#null_CS";
    private static StaticLoggerBinder SINGLETON;
    private static Object KEY;
    private boolean initialized;
    private LoggerContext defaultLoggerContext;
    private final ContextSelectorStaticBinder contextSelectorBinder;
    
    private StaticLoggerBinder() {
        this.initialized = false;
        this.defaultLoggerContext = new LoggerContext();
        this.contextSelectorBinder = ContextSelectorStaticBinder.getSingleton();
        this.defaultLoggerContext.setName("default");
    }
    
    public static StaticLoggerBinder getSingleton() {
        return StaticLoggerBinder.SINGLETON;
    }
    
    static void reset() {
        (StaticLoggerBinder.SINGLETON = new StaticLoggerBinder()).init();
    }
    
    void init() {
        try {
            try {
                new ContextInitializer(this.defaultLoggerContext).autoConfig();
            }
            catch (JoranException je) {
                Util.report("Failed to auto configure default logger context", je);
            }
            if (!StatusUtil.contextHasStatusListener(this.defaultLoggerContext)) {
                StatusPrinter.printInCaseOfErrorsOrWarnings(this.defaultLoggerContext);
            }
            this.contextSelectorBinder.init(this.defaultLoggerContext, StaticLoggerBinder.KEY);
            this.initialized = true;
        }
        catch (Throwable t) {
            Util.report("Failed to instantiate [" + LoggerContext.class.getName() + "]", t);
        }
    }
    
    public ILoggerFactory getLoggerFactory() {
        if (!this.initialized) {
            return this.defaultLoggerContext;
        }
        if (this.contextSelectorBinder.getContextSelector() == null) {
            throw new IllegalStateException("contextSelector cannot be null. See also http://logback.qos.ch/codes.html#null_CS");
        }
        return this.contextSelectorBinder.getContextSelector().getLoggerContext();
    }
    
    public String getLoggerFactoryClassStr() {
        return this.contextSelectorBinder.getClass().getName();
    }
    
    static {
        StaticLoggerBinder.REQUESTED_API_VERSION = "1.6";
        StaticLoggerBinder.SINGLETON = new StaticLoggerBinder();
        StaticLoggerBinder.KEY = new Object();
        StaticLoggerBinder.SINGLETON.init();
    }
}

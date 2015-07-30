// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.jul;

import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.util.logging.LogManager;
import com.newrelic.agent.deps.ch.qos.logback.classic.Level;
import com.newrelic.agent.deps.ch.qos.logback.classic.Logger;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import java.util.HashSet;
import java.util.Set;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggerContextListener;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public class LevelChangePropagator extends ContextAwareBase implements LoggerContextListener, LifeCycle
{
    private Set julLoggerSet;
    boolean isStarted;
    boolean resetJUL;
    
    public LevelChangePropagator() {
        this.julLoggerSet = new HashSet();
        this.isStarted = false;
        this.resetJUL = false;
    }
    
    public void setResetJUL(final boolean resetJUL) {
        this.resetJUL = resetJUL;
    }
    
    public boolean isResetResistant() {
        return false;
    }
    
    public void onStart(final LoggerContext context) {
    }
    
    public void onReset(final LoggerContext context) {
    }
    
    public void onStop(final LoggerContext context) {
    }
    
    public void onLevelChange(final Logger logger, final Level level) {
        this.propagate(logger, level);
    }
    
    private void propagate(final Logger logger, final Level level) {
        this.addInfo("Propagating " + level + " level on " + logger + " onto the JUL framework");
        final java.util.logging.Logger julLogger = JULHelper.asJULLogger(logger);
        this.julLoggerSet.add(julLogger);
        final java.util.logging.Level julLevel = JULHelper.asJULLevel(level);
        julLogger.setLevel(julLevel);
    }
    
    public void resetJULLevels() {
        final LogManager lm = LogManager.getLogManager();
        final Enumeration e = lm.getLoggerNames();
        while (e.hasMoreElements()) {
            final String loggerName = e.nextElement();
            final java.util.logging.Logger julLogger = lm.getLogger(loggerName);
            if (JULHelper.isRegularNonRootLogger(julLogger) && julLogger.getLevel() != null) {
                this.addInfo("Setting level of jul logger [" + loggerName + "] to null");
                julLogger.setLevel(null);
            }
        }
    }
    
    private void propagateExistingLoggerLevels() {
        final LoggerContext loggerContext = (LoggerContext)this.context;
        final List<Logger> loggerList = loggerContext.getLoggerList();
        for (final Logger l : loggerList) {
            if (l.getLevel() != null) {
                this.propagate(l, l.getLevel());
            }
        }
    }
    
    public void start() {
        if (this.resetJUL) {
            this.resetJULLevels();
        }
        this.propagateExistingLoggerLevels();
        this.isStarted = true;
    }
    
    public void stop() {
        this.isStarted = false;
    }
    
    public boolean isStarted() {
        return this.isStarted;
    }
}

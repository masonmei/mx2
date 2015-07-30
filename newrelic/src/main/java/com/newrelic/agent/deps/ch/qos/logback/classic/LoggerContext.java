// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.FilterReply;
import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.turbo.TurboFilter;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusListener;
import java.util.Comparator;
import java.util.Collections;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggerComparator;
import java.util.Collection;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.WarnStatus;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.LoggerNameUtil;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.TurboFilterList;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggerContextVO;
import java.util.Hashtable;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.LoggerContextListener;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.org.slf4j.ILoggerFactory;
import com.newrelic.agent.deps.ch.qos.logback.core.ContextBase;

public class LoggerContext extends ContextBase implements ILoggerFactory, LifeCycle
{
    final Logger root;
    private int size;
    private int noAppenderWarning;
    private final List<LoggerContextListener> loggerContextListenerList;
    private Hashtable<String, Logger> loggerCache;
    private LoggerContextVO loggerContextRemoteView;
    private final TurboFilterList turboFilterList;
    private boolean packagingDataEnabled;
    private int maxCallerDataDepth;
    boolean started;
    int resetCount;
    
    public LoggerContext() {
        this.noAppenderWarning = 0;
        this.loggerContextListenerList = new ArrayList<LoggerContextListener>();
        this.turboFilterList = new TurboFilterList();
        this.packagingDataEnabled = true;
        this.maxCallerDataDepth = 8;
        this.started = false;
        this.resetCount = 0;
        this.loggerCache = new Hashtable<String, Logger>();
        this.loggerContextRemoteView = new LoggerContextVO(this);
        (this.root = new Logger("ROOT", null, this)).setLevel(Level.DEBUG);
        this.loggerCache.put("ROOT", this.root);
        this.initEvaluatorMap();
        this.size = 1;
    }
    
    void initEvaluatorMap() {
        this.putObject("EVALUATOR_MAP", new HashMap());
    }
    
    private void syncRemoteView() {
        this.loggerContextRemoteView = new LoggerContextVO(this);
        for (final Logger logger : this.loggerCache.values()) {
            logger.buildRemoteView();
        }
    }
    
    public void putProperty(final String key, final String val) {
        super.putProperty(key, val);
        this.syncRemoteView();
    }
    
    public void setName(final String name) {
        super.setName(name);
        this.syncRemoteView();
    }
    
    public final Logger getLogger(final Class clazz) {
        return this.getLogger(clazz.getName());
    }
    
    public final Logger getLogger(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name argument cannot be null");
        }
        if ("ROOT".equalsIgnoreCase(name)) {
            return this.root;
        }
        int i = 0;
        Logger logger = this.root;
        Logger childLogger = this.loggerCache.get(name);
        if (childLogger != null) {
            return childLogger;
        }
        int h;
        do {
            h = LoggerNameUtil.getSeparatorIndexOf(name, i);
            String childName;
            if (h == -1) {
                childName = name;
            }
            else {
                childName = name.substring(0, h);
            }
            i = h + 1;
            synchronized (logger) {
                childLogger = logger.getChildByName(childName);
                if (childLogger == null) {
                    childLogger = logger.createChildByName(childName);
                    this.loggerCache.put(childName, childLogger);
                    this.incSize();
                }
            }
            logger = childLogger;
        } while (h != -1);
        return childLogger;
    }
    
    private void incSize() {
        ++this.size;
    }
    
    int size() {
        return this.size;
    }
    
    public Logger exists(final String name) {
        return this.loggerCache.get(name);
    }
    
    final void noAppenderDefinedWarning(final Logger logger) {
        if (this.noAppenderWarning++ == 0) {
            this.getStatusManager().add(new WarnStatus("No appenders present in context [" + this.getName() + "] for logger [" + logger.getName() + "].", logger));
        }
    }
    
    public List<Logger> getLoggerList() {
        final Collection<Logger> collection = this.loggerCache.values();
        final List<Logger> loggerList = new ArrayList<Logger>(collection);
        Collections.sort(loggerList, new LoggerComparator());
        return loggerList;
    }
    
    public LoggerContextVO getLoggerContextRemoteView() {
        return this.loggerContextRemoteView;
    }
    
    public void setPackagingDataEnabled(final boolean packagingDataEnabled) {
        this.packagingDataEnabled = packagingDataEnabled;
    }
    
    public boolean isPackagingDataEnabled() {
        return this.packagingDataEnabled;
    }
    
    public void reset() {
        ++this.resetCount;
        super.reset();
        this.initEvaluatorMap();
        this.root.recursiveReset();
        this.resetTurboFilterList();
        this.fireOnReset();
        this.resetListenersExceptResetResistant();
        this.resetStatusListeners();
    }
    
    private void resetStatusListeners() {
        final StatusManager sm = this.getStatusManager();
        for (final StatusListener sl : sm.getCopyOfStatusListenerList()) {
            sm.remove(sl);
        }
    }
    
    public TurboFilterList getTurboFilterList() {
        return this.turboFilterList;
    }
    
    public void addTurboFilter(final TurboFilter newFilter) {
        this.turboFilterList.add(newFilter);
    }
    
    public void resetTurboFilterList() {
        for (final TurboFilter tf : this.turboFilterList) {
            tf.stop();
        }
        this.turboFilterList.clear();
    }
    
    final FilterReply getTurboFilterChainDecision_0_3OrMore(final Marker marker, final Logger logger, final Level level, final String format, final Object[] params, final Throwable t) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, params, t);
    }
    
    final FilterReply getTurboFilterChainDecision_1(final Marker marker, final Logger logger, final Level level, final String format, final Object param, final Throwable t) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[] { param }, t);
    }
    
    final FilterReply getTurboFilterChainDecision_2(final Marker marker, final Logger logger, final Level level, final String format, final Object param1, final Object param2, final Throwable t) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[] { param1, param2 }, t);
    }
    
    public void addListener(final LoggerContextListener listener) {
        this.loggerContextListenerList.add(listener);
    }
    
    public void removeListener(final LoggerContextListener listener) {
        this.loggerContextListenerList.remove(listener);
    }
    
    private void resetListenersExceptResetResistant() {
        final List<LoggerContextListener> toRetain = new ArrayList<LoggerContextListener>();
        for (final LoggerContextListener lcl : this.loggerContextListenerList) {
            if (lcl.isResetResistant()) {
                toRetain.add(lcl);
            }
        }
        this.loggerContextListenerList.retainAll(toRetain);
    }
    
    private void resetAllListeners() {
        this.loggerContextListenerList.clear();
    }
    
    public List<LoggerContextListener> getCopyOfListenerList() {
        return new ArrayList<LoggerContextListener>(this.loggerContextListenerList);
    }
    
    void fireOnLevelChange(final Logger logger, final Level level) {
        for (final LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onLevelChange(logger, level);
        }
    }
    
    private void fireOnReset() {
        for (final LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onReset(this);
        }
    }
    
    private void fireOnStart() {
        for (final LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onStart(this);
        }
    }
    
    private void fireOnStop() {
        for (final LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onStop(this);
        }
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void start() {
        this.started = true;
        this.fireOnStart();
    }
    
    public void stop() {
        this.reset();
        this.fireOnStop();
        this.resetAllListeners();
        this.started = false;
    }
    
    public String toString() {
        return this.getClass().getName() + "[" + this.getName() + "]";
    }
    
    public int getMaxCallerDataDepth() {
        return this.maxCallerDataDepth;
    }
    
    public void setMaxCallerDataDepth(final int maxCallerDataDepth) {
        this.maxCallerDataDepth = maxCallerDataDepth;
    }
}

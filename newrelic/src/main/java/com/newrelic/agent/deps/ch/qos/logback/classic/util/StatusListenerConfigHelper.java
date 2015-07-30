// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusListener;
import com.newrelic.agent.deps.ch.qos.logback.core.status.OnConsoleStatusListener;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;

public class StatusListenerConfigHelper
{
    static void installIfAsked(final LoggerContext loggerContext) {
        final String slClass = OptionHelper.getSystemProperty("logback.statusListenerClass");
        if (!OptionHelper.isEmpty(slClass)) {
            addStatusListener(loggerContext, slClass);
        }
    }
    
    static void addStatusListener(final LoggerContext loggerContext, final String listenerClass) {
        StatusListener listener = null;
        if ("SYSOUT".equalsIgnoreCase(listenerClass)) {
            listener = new OnConsoleStatusListener();
        }
        else {
            try {
                listener = (StatusListener)OptionHelper.instantiateByClassName(listenerClass, StatusListener.class, loggerContext);
                if (listener instanceof ContextAware) {
                    ((ContextAware)listener).setContext(loggerContext);
                }
                if (listener instanceof LifeCycle) {
                    ((LifeCycle)listener).start();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (listener != null) {
            loggerContext.getStatusManager().add(listener);
        }
    }
}

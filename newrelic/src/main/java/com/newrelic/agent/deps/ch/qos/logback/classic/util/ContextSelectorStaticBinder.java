// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.util;

import java.lang.reflect.Constructor;
import com.newrelic.agent.deps.ch.qos.logback.core.util.Loader;
import java.lang.reflect.InvocationTargetException;
import com.newrelic.agent.deps.ch.qos.logback.classic.selector.ContextJNDISelector;
import com.newrelic.agent.deps.ch.qos.logback.classic.selector.DefaultContextSelector;
import com.newrelic.agent.deps.ch.qos.logback.core.util.OptionHelper;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.ch.qos.logback.classic.selector.ContextSelector;

public class ContextSelectorStaticBinder
{
    static ContextSelectorStaticBinder singleton;
    ContextSelector contextSelector;
    Object key;
    
    public static ContextSelectorStaticBinder getSingleton() {
        return ContextSelectorStaticBinder.singleton;
    }
    
    public void init(final LoggerContext defaultLoggerContext, final Object key) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (this.key == null) {
            this.key = key;
        }
        else if (this.key != key) {
            throw new IllegalAccessException("Only certain classes can access this method.");
        }
        final String contextSelectorStr = OptionHelper.getSystemProperty("logback.ContextSelector");
        if (contextSelectorStr == null) {
            this.contextSelector = new DefaultContextSelector(defaultLoggerContext);
        }
        else if (contextSelectorStr.equals("JNDI")) {
            this.contextSelector = new ContextJNDISelector(defaultLoggerContext);
        }
        else {
            this.contextSelector = dynamicalContextSelector(defaultLoggerContext, contextSelectorStr);
        }
    }
    
    static ContextSelector dynamicalContextSelector(final LoggerContext defaultLoggerContext, final String contextSelectorStr) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Class<?> contextSelectorClass = (Class<?>)Loader.loadClass(contextSelectorStr);
        final Constructor cons = contextSelectorClass.getConstructor(LoggerContext.class);
        return cons.newInstance(defaultLoggerContext);
    }
    
    public ContextSelector getContextSelector() {
        return this.contextSelector;
    }
    
    static {
        ContextSelectorStaticBinder.singleton = new ContextSelectorStaticBinder();
    }
}

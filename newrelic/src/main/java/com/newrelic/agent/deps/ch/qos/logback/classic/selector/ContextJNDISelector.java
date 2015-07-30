// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.selector;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.classic.joran.JoranConfigurator;
import com.newrelic.agent.deps.ch.qos.logback.core.util.Loader;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusManager;
import com.newrelic.agent.deps.ch.qos.logback.core.status.WarnStatus;
import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.status.InfoStatus;
import java.net.URL;
import com.newrelic.agent.deps.ch.qos.logback.core.util.StatusPrinter;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;
import com.newrelic.agent.deps.ch.qos.logback.core.status.StatusUtil;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.ContextInitializer;
import javax.naming.NamingException;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.JNDIUtil;
import java.util.Collections;
import java.util.HashMap;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import java.util.Map;

public class ContextJNDISelector implements ContextSelector
{
    private final Map<String, LoggerContext> synchronizedContextMap;
    private final LoggerContext defaultContext;
    private static final ThreadLocal<LoggerContext> threadLocal;
    
    public ContextJNDISelector(final LoggerContext context) {
        this.synchronizedContextMap = Collections.synchronizedMap(new HashMap<String, LoggerContext>());
        this.defaultContext = context;
    }
    
    public LoggerContext getDefaultLoggerContext() {
        return this.defaultContext;
    }
    
    public LoggerContext detachLoggerContext(final String loggerContextName) {
        return this.synchronizedContextMap.remove(loggerContextName);
    }
    
    public LoggerContext getLoggerContext() {
        String contextName = null;
        javax.naming.Context ctx = null;
        final LoggerContext lc = ContextJNDISelector.threadLocal.get();
        if (lc != null) {
            return lc;
        }
        try {
            ctx = JNDIUtil.getInitialContext();
            contextName = JNDIUtil.lookup(ctx, "java:comp/env/logback/context-name");
        }
        catch (NamingException ex) {}
        if (contextName == null) {
            return this.defaultContext;
        }
        LoggerContext loggerContext = this.synchronizedContextMap.get(contextName);
        if (loggerContext == null) {
            loggerContext = new LoggerContext();
            loggerContext.setName(contextName);
            this.synchronizedContextMap.put(contextName, loggerContext);
            final URL url = this.findConfigFileURL(ctx, loggerContext);
            if (url != null) {
                this.configureLoggerContextByURL(loggerContext, url);
            }
            else {
                try {
                    new ContextInitializer(loggerContext).autoConfig();
                }
                catch (JoranException ex2) {}
            }
            if (!StatusUtil.contextHasStatusListener(loggerContext)) {
                StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
            }
        }
        return loggerContext;
    }
    
    private String conventionalConfigFileName(final String contextName) {
        return "logback-" + contextName + ".xml";
    }
    
    private URL findConfigFileURL(final javax.naming.Context ctx, final LoggerContext loggerContext) {
        final StatusManager sm = loggerContext.getStatusManager();
        final String jndiEntryForConfigResource = JNDIUtil.lookup(ctx, "java:comp/env/logback/configuration-resource");
        if (jndiEntryForConfigResource != null) {
            sm.add(new InfoStatus("Searching for [" + jndiEntryForConfigResource + "]", this));
            final URL url = this.urlByResourceName(sm, jndiEntryForConfigResource);
            if (url == null) {
                final String msg = "The jndi resource [" + jndiEntryForConfigResource + "] for context [" + loggerContext.getName() + "] does not lead to a valid file";
                sm.add(new WarnStatus(msg, this));
            }
            return url;
        }
        final String resourceByConvention = this.conventionalConfigFileName(loggerContext.getName());
        return this.urlByResourceName(sm, resourceByConvention);
    }
    
    private URL urlByResourceName(final StatusManager sm, final String resourceName) {
        sm.add(new InfoStatus("Searching for [" + resourceName + "]", this));
        final URL url = Loader.getResource(resourceName, Loader.getTCL());
        if (url != null) {
            return url;
        }
        return Loader.getResourceBySelfClassLoader(resourceName);
    }
    
    private void configureLoggerContextByURL(final LoggerContext context, final URL url) {
        try {
            final JoranConfigurator configurator = new JoranConfigurator();
            context.reset();
            configurator.setContext(context);
            configurator.doConfigure(url);
        }
        catch (JoranException ex) {}
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
    
    public List<String> getContextNames() {
        final List<String> list = new ArrayList<String>();
        list.addAll(this.synchronizedContextMap.keySet());
        return list;
    }
    
    public LoggerContext getLoggerContext(final String name) {
        return this.synchronizedContextMap.get(name);
    }
    
    public int getCount() {
        return this.synchronizedContextMap.size();
    }
    
    public void setLocalContext(final LoggerContext context) {
        ContextJNDISelector.threadLocal.set(context);
    }
    
    public void removeLocalContext() {
        ContextJNDISelector.threadLocal.remove();
    }
    
    static {
        threadLocal = new ThreadLocal<LoggerContext>();
    }
}

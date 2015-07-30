// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.selector.servlet;

import com.newrelic.agent.deps.org.slf4j.Logger;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import com.newrelic.agent.deps.ch.qos.logback.classic.selector.ContextSelector;
import javax.naming.Context;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import javax.naming.NamingException;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.JNDIUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextDetachingSCL implements ServletContextListener
{
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        String loggerContextName = null;
        try {
            final Context ctx = JNDIUtil.getInitialContext();
            loggerContextName = JNDIUtil.lookup(ctx, "java:comp/env/logback/context-name");
        }
        catch (NamingException ex) {}
        if (loggerContextName != null) {
            System.out.println("About to detach context named " + loggerContextName);
            final ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
            final LoggerContext context = selector.detachLoggerContext(loggerContextName);
            if (context != null) {
                final Logger logger = context.getLogger("ROOT");
                logger.warn("Stopping logger context " + loggerContextName);
                context.stop();
            }
            else {
                System.out.println("No context named " + loggerContextName + " was found.");
            }
        }
    }
    
    public void contextInitialized(final ServletContextEvent arg0) {
    }
}

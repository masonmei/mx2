// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.selector.servlet;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import com.newrelic.agent.deps.ch.qos.logback.classic.selector.ContextSelector;
import com.newrelic.agent.deps.ch.qos.logback.classic.selector.ContextJNDISelector;
import com.newrelic.agent.deps.ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import com.newrelic.agent.deps.org.slf4j.LoggerFactory;
import com.newrelic.agent.deps.ch.qos.logback.classic.LoggerContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.Filter;

public class LoggerContextFilter implements Filter
{
    public void destroy() {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        final ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
        ContextJNDISelector sel = null;
        if (selector instanceof ContextJNDISelector) {
            sel = (ContextJNDISelector)selector;
            sel.setLocalContext(lc);
        }
        try {
            chain.doFilter(request, response);
        }
        finally {
            if (sel != null) {
                sel.removeLocalContext();
            }
        }
    }
    
    public void init(final FilterConfig arg0) throws ServletException {
    }
}

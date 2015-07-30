// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import java.util.logging.Level;
import com.newrelic.agent.deps.org.apache.commons.logging.LogConfigurationException;
import com.newrelic.agent.Agent;
import com.newrelic.agent.deps.org.apache.commons.logging.Log;
import com.newrelic.agent.deps.org.apache.commons.logging.LogFactory;

public class ApacheCommonsAdaptingLogFactory extends LogFactory
{
    public static final IAgentLogger LOG;
    
    public Object getAttribute(final String name) {
        return null;
    }
    
    public String[] getAttributeNames() {
        return new String[0];
    }
    
    public Log getInstance(final Class clazz) throws LogConfigurationException {
        return new LogAdapter(clazz, Agent.LOG);
    }
    
    public Log getInstance(final String name) throws LogConfigurationException {
        return new LogAdapter(name, Agent.LOG);
    }
    
    public void release() {
    }
    
    public void removeAttribute(final String name) {
    }
    
    public void setAttribute(final String name, final Object value) {
    }
    
    static {
        LOG = AgentLogManager.getLogger();
    }
    
    private class LogAdapter implements Log
    {
        private final IAgentLogger logger;
        
        public LogAdapter(final Class<?> clazz, final IAgentLogger logger) {
            this.logger = logger.getChildLogger(clazz);
        }
        
        public LogAdapter(final String name, final IAgentLogger logger) {
            this.logger = logger.getChildLogger(name);
        }
        
        public boolean isDebugEnabled() {
            return Agent.isDebugEnabled() && this.logger.isDebugEnabled();
        }
        
        public boolean isErrorEnabled() {
            return this.isDebugEnabled() && this.logger.isLoggable(Level.SEVERE);
        }
        
        public boolean isFatalEnabled() {
            return this.isDebugEnabled() && this.logger.isLoggable(Level.SEVERE);
        }
        
        public boolean isInfoEnabled() {
            return this.isDebugEnabled() && this.logger.isLoggable(Level.INFO);
        }
        
        public boolean isTraceEnabled() {
            return this.isDebugEnabled() && this.logger.isLoggable(Level.FINEST);
        }
        
        public boolean isWarnEnabled() {
            return this.isDebugEnabled() && this.logger.isLoggable(Level.WARNING);
        }
        
        public void trace(final Object message) {
            if (this.isDebugEnabled()) {
                this.logger.trace(message.toString());
            }
        }
        
        public void trace(final Object message, final Throwable t) {
            if (this.isDebugEnabled()) {
                this.logger.log(Level.FINEST, t, message.toString(), new Object[0]);
            }
        }
        
        public void debug(final Object message) {
            if (this.isDebugEnabled()) {
                this.logger.debug(message.toString());
            }
        }
        
        public void debug(final Object message, final Throwable t) {
            if (this.isDebugEnabled()) {
                this.logger.log(Level.FINEST, "{0} : {1}", new Object[] { message, t });
            }
        }
        
        public void info(final Object message) {
            if (this.isDebugEnabled()) {
                this.logger.info(message.toString());
            }
        }
        
        public void info(final Object message, final Throwable t) {
            if (this.isDebugEnabled()) {
                this.logger.log(Level.INFO, "{0} : {1}", new Object[] { message, t });
            }
        }
        
        public void warn(final Object message) {
            if (this.isDebugEnabled()) {
                this.logger.warning(message.toString());
            }
        }
        
        public void warn(final Object message, final Throwable t) {
            if (this.isDebugEnabled()) {
                this.logger.log(Level.WARNING, "{0} : {1}", new Object[] { message, t });
            }
        }
        
        public void error(final Object message) {
            if (this.isDebugEnabled()) {
                this.logger.error(message.toString());
            }
        }
        
        public void error(final Object message, final Throwable t) {
            if (this.isDebugEnabled()) {
                this.logger.log(Level.SEVERE, "{0} : {1}", new Object[] { message, t });
            }
        }
        
        public void fatal(final Object message) {
            if (this.isDebugEnabled()) {
                this.logger.severe(message.toString());
            }
        }
        
        public void fatal(final Object message, final Throwable t) {
            if (this.isDebugEnabled()) {
                this.logger.log(Level.SEVERE, "{0} : {1}", new Object[] { message, t });
            }
        }
    }
}

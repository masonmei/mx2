// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.jmx;

import com.newrelic.agent.logging.AgentLogManager;
import com.newrelic.agent.IAgent;
import com.newrelic.agent.IRPMService;
import java.text.MessageFormat;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.Agent;
import javax.management.NotCompliantMBeanException;

public class AgentMBeanImpl extends BaseMBean implements AgentMBean
{
    protected AgentMBeanImpl() throws NotCompliantMBeanException {
        super(AgentMBean.class);
    }
    
    public boolean shutdown() {
        Agent.LOG.info("AgentMBean is shutting down the Agent");
        this.getAgent().shutdown();
        return true;
    }
    
    public boolean reconnect() {
        try {
            final IRPMService rpmService = ServiceFactory.getRPMService();
            Agent.LOG.info(MessageFormat.format("AgentMBean is reconnecting {0}", rpmService.getApplicationName()));
            rpmService.reconnect();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    private IAgent getAgent() {
        return ServiceFactory.getAgent();
    }
    
    public boolean isStarted() {
        return this.getAgent().isStarted();
    }
    
    public boolean isConnected() {
        return ServiceFactory.getRPMService().isConnected();
    }
    
    public boolean connect() {
        try {
            final IRPMService rpmService = ServiceFactory.getRPMService();
            Agent.LOG.info(MessageFormat.format("AgentMBean is connecting {0}", rpmService.getApplicationName()));
            rpmService.launch();
            return true;
        }
        catch (Exception ex) {
            Agent.LOG.severe("Connect error: " + ex.getMessage());
            return false;
        }
    }
    
    public String setLogLevel(final String level) {
        AgentLogManager.setLogLevel(level);
        Agent.LOG.info(MessageFormat.format("AgentMBean is setting log level to {0}", level));
        return level;
    }
    
    public String getLogLevel() {
        return AgentLogManager.getLogLevel();
    }
}

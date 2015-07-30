// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.environment;

import com.newrelic.agent.logging.AgentLogManager;
import java.lang.management.ManagementFactory;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.service.AbstractService;

public class EnvironmentServiceImpl extends AbstractService implements EnvironmentService
{
    private final int processPID;
    private final Environment environment;
    
    public EnvironmentServiceImpl() {
        super(EnvironmentService.class.getSimpleName());
        this.processPID = this.initProcessPID();
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        this.environment = this.initEnvironment(config);
    }
    
    protected void doStart() {
    }
    
    protected void doStop() {
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public int getProcessPID() {
        return this.processPID;
    }
    
    private int initProcessPID() {
        final String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        final String[] split = runtimeName.split("@");
        if (split.length > 1) {
            return Integer.parseInt(split[0]);
        }
        return 0;
    }
    
    public Environment getEnvironment() {
        return this.environment;
    }
    
    private Environment initEnvironment(final AgentConfig config) {
        String logFilePath = AgentLogManager.getLogFilePath();
        if (logFilePath == null) {
            logFilePath = "";
        }
        return new Environment(config, logFilePath);
    }
}

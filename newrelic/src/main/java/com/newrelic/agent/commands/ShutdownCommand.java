// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

import java.util.Collections;
import com.newrelic.agent.Agent;
import java.util.Map;
import com.newrelic.agent.IRPMService;
import com.newrelic.agent.IAgent;

public class ShutdownCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "shutdown";
    private final IAgent agent;
    
    public ShutdownCommand(final IAgent agent) {
        super("shutdown");
        this.agent = agent;
    }
    
    public Map process(final IRPMService rpmService, final Map arguments) throws CommandException {
        Agent.LOG.info("ShutdownCommand is shutting down the Agent");
        this.agent.shutdownAsync();
        return Collections.EMPTY_MAP;
    }
}

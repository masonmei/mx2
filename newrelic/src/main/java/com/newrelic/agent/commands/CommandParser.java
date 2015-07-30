// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

import com.newrelic.agent.IAgent;
import com.newrelic.agent.config.AgentConfig;
import java.util.Iterator;
import com.newrelic.agent.util.JSONException;
import java.util.Collections;
import com.newrelic.agent.Agent;
import java.util.List;
import com.newrelic.agent.IRPMService;
import java.text.MessageFormat;
import java.util.logging.Level;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.stats.StatsEngine;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;

public class CommandParser extends AbstractService implements HarvestListener
{
    private final Map<String, Command> commands;
    private boolean enabled;
    
    public CommandParser() {
        super(CommandParser.class.getSimpleName());
        this.commands = new HashMap<String, Command>();
        this.enabled = true;
    }
    
    public void addCommands(final Command... commands) {
        for (final Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }
    
    public void beforeHarvest(final String appName, final StatsEngine statsEngine) {
        final IRPMService rpmService = ServiceFactory.getRPMService(appName);
        List<List<?>> commands;
        try {
            commands = rpmService.getAgentCommands();
        }
        catch (Exception e) {
            this.getLogger().log(Level.FINE, "Unable to get agent commands - {0}", new Object[] { e.toString() });
            this.getLogger().log(Level.FINEST, (Throwable)e, e.toString(), new Object[0]);
            return;
        }
        final Map<Long, Object> commandResults = this.processCommands(rpmService, commands);
        try {
            rpmService.sendCommandResults(commandResults);
        }
        catch (Exception e2) {
            final String msg = MessageFormat.format("Unable to send agent command feedback.  Command results: {0}", commandResults.toString());
            this.getLogger().fine(msg);
        }
    }
    
    public void afterHarvest(final String appName) {
    }
    
    Command getCommand(final String name) throws UnknownCommand {
        Agent.LOG.finer(MessageFormat.format("Process command \"{0}\"", name));
        final Command c = this.commands.get(name);
        if (c == null) {
            throw new UnknownCommand("Unknown command " + name);
        }
        return c;
    }
    
    Map<Long, Object> processCommands(final IRPMService rpmService, final List<List<?>> commands) {
        final Map<Long, Object> results = new HashMap<Long, Object>();
        int count = 0;
        for (final List<?> agentCommand : commands) {
            if (agentCommand.size() == 2) {
                final Object id = agentCommand.get(0);
                if (id instanceof Number) {
                    try {
                        final Map<?, ?> commandMap = (Map<?, ?>)agentCommand.get(1);
                        final String name = (String)commandMap.get("name");
                        Map<?, ?> args = (Map<?, ?>)commandMap.get("arguments");
                        if (args == null) {
                            args = (Map<?, ?>)Collections.EMPTY_MAP;
                        }
                        final Command command = this.getCommand(name);
                        final Object returnValue = command.process(rpmService, args);
                        results.put(((Number)id).longValue(), returnValue);
                        this.getLogger().finer(MessageFormat.format("Agent command \"{0}\" return value: {1}", name, returnValue));
                    }
                    catch (Exception e) {
                        this.getLogger().severe(MessageFormat.format("Unable to parse command : {0}", e.toString()));
                        this.getLogger().fine(MessageFormat.format("Unable to parse command", e));
                        results.put(((Number)id).longValue(), new JSONException(e));
                    }
                }
                else {
                    this.invalidCommand(rpmService, count, "Invalid command id " + id, agentCommand);
                }
            }
            else {
                this.invalidCommand(rpmService, count, "Unable to parse command", agentCommand);
            }
            ++count;
        }
        return results;
    }
    
    private void invalidCommand(final IRPMService rpmService, final int index, final String message, final List<?> agentCommand) {
        this.getLogger().severe(MessageFormat.format("Unable to parse command : {0} ({1})", message, agentCommand.toString()));
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    protected void doStart() {
        final AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
        final IAgent agent = ServiceFactory.getAgent();
        this.addCommands(new ShutdownCommand(agent), new RestartCommand());
        this.setEnabled(config);
        if (this.isEnabled()) {
            ServiceFactory.getHarvestService().addHarvestListener(this);
        }
        else {
            this.getLogger().log(Level.CONFIG, "The command parser is disabled");
        }
    }
    
    private void setEnabled(final AgentConfig agentConfig) {
        try {
            final Map<?, ?> props = agentConfig.getProperty("command_parser");
            if (props != null) {
                final Boolean enabled = (Boolean)props.get("enabled");
                this.enabled = (enabled != null && enabled);
            }
        }
        catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, "Unable to parse the command_parser section in newrelic.yml");
        }
    }
    
    protected void doStop() {
    }
}

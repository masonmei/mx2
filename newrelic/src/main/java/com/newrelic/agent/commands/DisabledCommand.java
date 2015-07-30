// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

import java.util.HashMap;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.util.Map;
import com.newrelic.agent.IRPMService;
import java.text.MessageFormat;

public final class DisabledCommand extends AbstractCommand
{
    private final String errorMessage;
    
    public DisabledCommand(final String name) {
        this(name, MessageFormat.format("Command \"{0}\" is disabled", name));
    }
    
    public DisabledCommand(final String name, final String errorMessage) {
        super(name);
        this.errorMessage = errorMessage;
    }
    
    public Map process(final IRPMService rpmService, final Map arguments) throws CommandException {
        Agent.LOG.log(Level.INFO, this.errorMessage);
        final Map<String, String> map = new HashMap<String, String>();
        map.put("error", this.errorMessage);
        return map;
    }
}

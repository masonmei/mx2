// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

import java.util.Collections;
import java.util.Map;
import com.newrelic.agent.IRPMService;

public class RestartCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "restart";
    
    public RestartCommand() {
        super("restart");
    }
    
    public Map process(final IRPMService rpmService, final Map arguments) throws CommandException {
        rpmService.reconnect();
        return Collections.EMPTY_MAP;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

public abstract class AbstractCommand implements Command
{
    private final String commandName;
    
    public AbstractCommand(final String commandName) {
        this.commandName = commandName;
    }
    
    public final String getName() {
        return this.commandName;
    }
}

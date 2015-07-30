// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

public class UnknownCommand extends CommandException
{
    private static final long serialVersionUID = 2152047474983639450L;
    
    public UnknownCommand(final String message) {
        super(message);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.commands;

public class CommandException extends Exception
{
    private static final long serialVersionUID = 2152047474983639450L;
    
    public CommandException(final String message) {
        super(message);
    }
    
    public CommandException(final String message, final Throwable t) {
        super(message, t);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

public class ForceDisconnectException extends ServerCommandException
{
    private static final long serialVersionUID = 7001395828662633469L;
    
    public ForceDisconnectException(final String message) {
        super(message);
    }
}

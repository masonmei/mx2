// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

public class InternalLimitExceeded extends ServerCommandException
{
    private static final long serialVersionUID = -6876385842601935066L;
    
    public InternalLimitExceeded(final String message) {
        super(message);
    }
}

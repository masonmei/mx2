// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

public class AgentError extends Error
{
    private static final long serialVersionUID = -2870952056899794642L;
    
    public AgentError(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public AgentError(final String message) {
        super(message);
    }
    
    public AgentError(final Throwable cause) {
        super(cause);
    }
}

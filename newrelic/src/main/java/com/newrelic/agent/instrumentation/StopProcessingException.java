// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

public class StopProcessingException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public StopProcessingException(final String msg) {
        super(msg);
    }
}

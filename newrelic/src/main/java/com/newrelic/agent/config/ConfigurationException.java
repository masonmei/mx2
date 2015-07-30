// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.config;

public class ConfigurationException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public ConfigurationException(final String message) {
        super(message);
    }
    
    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

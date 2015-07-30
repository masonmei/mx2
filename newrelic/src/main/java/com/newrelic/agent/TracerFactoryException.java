// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

public class TracerFactoryException extends Exception
{
    private static final long serialVersionUID = -6103280171903439862L;
    
    public TracerFactoryException(final String message) {
        super(message);
    }
    
    public TracerFactoryException(final String message, final Exception e) {
        super(message, e);
    }
}

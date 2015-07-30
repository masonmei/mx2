// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

public class IgnoreSilentlyException extends Exception
{
    private static final long serialVersionUID = 7001395828662633469L;
    
    public IgnoreSilentlyException(final String message) {
        super(message);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent;

public class PostTooBigException extends IgnoreSilentlyException
{
    private static final long serialVersionUID = 7001395828662633469L;
    
    public PostTooBigException(final String message) {
        super(message);
    }
}

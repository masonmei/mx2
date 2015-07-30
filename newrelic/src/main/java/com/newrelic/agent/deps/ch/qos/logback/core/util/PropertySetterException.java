// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.util;

public class PropertySetterException extends Exception
{
    private static final long serialVersionUID = -2771077768281663949L;
    
    public PropertySetterException(final String msg) {
        super(msg);
    }
    
    public PropertySetterException(final Throwable rootCause) {
        super(rootCause);
    }
    
    public PropertySetterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

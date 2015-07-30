// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.jaxb;

public class UnmarshalException extends Exception
{
    private static final long serialVersionUID = -3785749805564625068L;
    
    public UnmarshalException(final Exception ex) {
        super(ex);
    }
    
    public UnmarshalException(final String message) {
        super(message);
    }
}

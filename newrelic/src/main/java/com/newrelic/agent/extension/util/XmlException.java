// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.util;

public class XmlException extends Exception
{
    private static final long serialVersionUID = 8308599094191068541L;
    
    public XmlException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public XmlException(final String message) {
        super(message);
    }
}

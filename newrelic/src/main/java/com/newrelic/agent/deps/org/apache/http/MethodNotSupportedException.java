// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public class MethodNotSupportedException extends HttpException
{
    private static final long serialVersionUID = 3365359036840171201L;
    
    public MethodNotSupportedException(final String message) {
        super(message);
    }
    
    public MethodNotSupportedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
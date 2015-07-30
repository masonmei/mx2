// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public class HttpException extends Exception
{
    private static final long serialVersionUID = -5437299376222011036L;
    
    public HttpException() {
    }
    
    public HttpException(final String message) {
        super(message);
    }
    
    public HttpException(final String message, final Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}

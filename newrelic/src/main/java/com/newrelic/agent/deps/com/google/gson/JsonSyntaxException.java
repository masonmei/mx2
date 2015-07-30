// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.gson;

public final class JsonSyntaxException extends JsonParseException
{
    private static final long serialVersionUID = 1L;
    
    public JsonSyntaxException(final String msg) {
        super(msg);
    }
    
    public JsonSyntaxException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public JsonSyntaxException(final Throwable cause) {
        super(cause);
    }
}

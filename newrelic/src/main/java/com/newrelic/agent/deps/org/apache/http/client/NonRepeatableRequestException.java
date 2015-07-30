// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;

@Immutable
public class NonRepeatableRequestException extends ProtocolException
{
    private static final long serialVersionUID = 82685265288806048L;
    
    public NonRepeatableRequestException() {
    }
    
    public NonRepeatableRequestException(final String message) {
        super(message);
    }
    
    public NonRepeatableRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

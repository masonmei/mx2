// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.execchain;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import java.io.InterruptedIOException;

@Immutable
public class RequestAbortedException extends InterruptedIOException
{
    private static final long serialVersionUID = 4973849966012490112L;
    
    public RequestAbortedException(final String message) {
        super(message);
    }
    
    public RequestAbortedException(final String message, final Throwable cause) {
        super(message);
        if (cause != null) {
            this.initCause(cause);
        }
    }
}

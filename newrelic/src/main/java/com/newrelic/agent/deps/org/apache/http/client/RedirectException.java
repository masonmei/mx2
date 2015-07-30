// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.client;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.ProtocolException;

@Immutable
public class RedirectException extends ProtocolException
{
    private static final long serialVersionUID = 4418824536372559326L;
    
    public RedirectException() {
    }
    
    public RedirectException(final String message) {
        super(message);
    }
    
    public RedirectException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

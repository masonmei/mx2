// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.impl.auth;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;
import com.newrelic.agent.deps.org.apache.http.auth.AuthenticationException;

@Immutable
public class NTLMEngineException extends AuthenticationException
{
    private static final long serialVersionUID = 6027981323731768824L;
    
    public NTLMEngineException() {
    }
    
    public NTLMEngineException(final String message) {
        super(message);
    }
    
    public NTLMEngineException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

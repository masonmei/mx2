// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.conn;

import com.newrelic.agent.deps.org.apache.http.annotation.Immutable;

@Immutable
public class ConnectionPoolTimeoutException extends ConnectTimeoutException
{
    private static final long serialVersionUID = -7898874842020245128L;
    
    public ConnectionPoolTimeoutException() {
    }
    
    public ConnectionPoolTimeoutException(final String message) {
        super(message);
    }
}

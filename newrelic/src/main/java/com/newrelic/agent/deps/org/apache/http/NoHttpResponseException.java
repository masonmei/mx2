// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import java.io.IOException;

public class NoHttpResponseException extends IOException
{
    private static final long serialVersionUID = -7658940387386078766L;
    
    public NoHttpResponseException(final String message) {
        super(message);
    }
}

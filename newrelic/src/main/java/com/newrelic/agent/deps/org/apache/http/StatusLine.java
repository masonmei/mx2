// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public interface StatusLine
{
    ProtocolVersion getProtocolVersion();
    
    int getStatusCode();
    
    String getReasonPhrase();
}

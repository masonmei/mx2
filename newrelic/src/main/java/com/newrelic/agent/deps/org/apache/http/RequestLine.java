// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public interface RequestLine
{
    String getMethod();
    
    ProtocolVersion getProtocolVersion();
    
    String getUri();
}

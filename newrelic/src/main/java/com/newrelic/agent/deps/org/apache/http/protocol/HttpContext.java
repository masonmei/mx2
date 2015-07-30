// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

public interface HttpContext
{
    public static final String RESERVED_PREFIX = "http.";
    
    Object getAttribute(String p0);
    
    void setAttribute(String p0, Object p1);
    
    Object removeAttribute(String p0);
}

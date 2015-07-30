// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public interface HttpRequestFactory
{
    HttpRequest newHttpRequest(RequestLine p0) throws MethodNotSupportedException;
    
    HttpRequest newHttpRequest(String p0, String p1) throws MethodNotSupportedException;
}

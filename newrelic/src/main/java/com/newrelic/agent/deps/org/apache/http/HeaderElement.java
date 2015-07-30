// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

public interface HeaderElement
{
    String getName();
    
    String getValue();
    
    NameValuePair[] getParameters();
    
    NameValuePair getParameterByName(String p0);
    
    int getParameterCount();
    
    NameValuePair getParameter(int p0);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http;

import com.newrelic.agent.deps.org.apache.http.params.HttpParams;

public interface HttpMessage
{
    ProtocolVersion getProtocolVersion();
    
    boolean containsHeader(String p0);
    
    Header[] getHeaders(String p0);
    
    Header getFirstHeader(String p0);
    
    Header getLastHeader(String p0);
    
    Header[] getAllHeaders();
    
    void addHeader(Header p0);
    
    void addHeader(String p0, String p1);
    
    void setHeader(Header p0);
    
    void setHeader(String p0, String p1);
    
    void setHeaders(Header[] p0);
    
    void removeHeader(Header p0);
    
    void removeHeaders(String p0);
    
    HeaderIterator headerIterator();
    
    HeaderIterator headerIterator(String p0);
    
    @Deprecated
    HttpParams getParams();
    
    @Deprecated
    void setParams(HttpParams p0);
}

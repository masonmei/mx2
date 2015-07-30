// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import java.util.List;
import com.newrelic.agent.deps.org.apache.http.HttpRequestInterceptor;

@Deprecated
public interface HttpRequestInterceptorList
{
    void addRequestInterceptor(HttpRequestInterceptor p0);
    
    void addRequestInterceptor(HttpRequestInterceptor p0, int p1);
    
    int getRequestInterceptorCount();
    
    HttpRequestInterceptor getRequestInterceptor(int p0);
    
    void clearRequestInterceptors();
    
    void removeRequestInterceptorByClass(Class<? extends HttpRequestInterceptor> p0);
    
    void setInterceptors(List<?> p0);
}

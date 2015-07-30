// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.protocol;

import java.util.List;
import com.newrelic.agent.deps.org.apache.http.HttpResponseInterceptor;

@Deprecated
public interface HttpResponseInterceptorList
{
    void addResponseInterceptor(HttpResponseInterceptor p0);
    
    void addResponseInterceptor(HttpResponseInterceptor p0, int p1);
    
    int getResponseInterceptorCount();
    
    HttpResponseInterceptor getResponseInterceptor(int p0);
    
    void clearResponseInterceptors();
    
    void removeResponseInterceptorByClass(Class<? extends HttpResponseInterceptor> p0);
    
    void setInterceptors(List<?> p0);
}

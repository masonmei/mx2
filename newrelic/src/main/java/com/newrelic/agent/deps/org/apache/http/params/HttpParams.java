// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.params;

@Deprecated
public interface HttpParams
{
    Object getParameter(String p0);
    
    HttpParams setParameter(String p0, Object p1);
    
    HttpParams copy();
    
    boolean removeParameter(String p0);
    
    long getLongParameter(String p0, long p1);
    
    HttpParams setLongParameter(String p0, long p1);
    
    int getIntParameter(String p0, int p1);
    
    HttpParams setIntParameter(String p0, int p1);
    
    double getDoubleParameter(String p0, double p1);
    
    HttpParams setDoubleParameter(String p0, double p1);
    
    boolean getBooleanParameter(String p0, boolean p1);
    
    HttpParams setBooleanParameter(String p0, boolean p1);
    
    boolean isParameterTrue(String p0);
    
    boolean isParameterFalse(String p0);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import javax.management.MBeanServer;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public interface PrivateApi
{
    void setAppServerPort(int p0);
    
    void setInstanceName(String p0);
    
    Closeable addSampler(Runnable p0, int p1, TimeUnit p2);
    
    void setServerInfo(String p0, String p1);
    
    void setServerInfo(String p0);
    
    void addCustomAttribute(String p0, Number p1);
    
    void addCustomAttribute(String p0, String p1);
    
    void addTracerParameter(String p0, Number p1);
    
    void addMBeanServer(MBeanServer p0);
    
    void removeMBeanServer(MBeanServer p0);
    
    void reportHTTPError(String p0, int p1, String p2);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import javax.management.MBeanServer;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public class NoOpPrivateApi implements PrivateApi
{
    public void setAppServerPort(final int port) {
    }
    
    public void setInstanceName(final String instanceName) {
    }
    
    public Closeable addSampler(final Runnable sampler, final int period, final TimeUnit minutes) {
        return null;
    }
    
    public void setServerInfo(final String serverInfo) {
    }
    
    public void setServerInfo(final String dispatcherName, final String version) {
    }
    
    public void addCustomAttribute(final String key, final Number value) {
    }
    
    public void addCustomAttribute(final String key, final String value) {
    }
    
    public void addMBeanServer(final MBeanServer server) {
    }
    
    public void removeMBeanServer(final MBeanServer serverToRemove) {
    }
    
    public void addTracerParameter(final String key, final Number value) {
    }
    
    public void reportHTTPError(final String message, final int statusCode, final String uri) {
    }
}

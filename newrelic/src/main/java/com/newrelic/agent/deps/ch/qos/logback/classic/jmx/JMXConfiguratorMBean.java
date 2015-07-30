// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.jmx;

import java.util.List;
import java.net.URL;
import java.io.FileNotFoundException;
import com.newrelic.agent.deps.ch.qos.logback.core.joran.spi.JoranException;

public interface JMXConfiguratorMBean
{
    void reloadDefaultConfiguration() throws JoranException;
    
    void reloadByFileName(String p0) throws JoranException, FileNotFoundException;
    
    void reloadByURL(URL p0) throws JoranException;
    
    void setLoggerLevel(String p0, String p1);
    
    String getLoggerLevel(String p0);
    
    String getLoggerEffectiveLevel(String p0);
    
    List<String> getLoggerList();
    
    List<String> getStatuses();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.logging;

import java.util.logging.Level;
import com.newrelic.api.agent.Logger;

public interface IAgentLogger extends Logger
{
    void severe(String p0);
    
    void error(String p0);
    
    void warning(String p0);
    
    void info(String p0);
    
    void config(String p0);
    
    void fine(String p0);
    
    void finer(String p0);
    
    void finest(String p0);
    
    void debug(String p0);
    
    void trace(String p0);
    
    boolean isFineEnabled();
    
    boolean isFinerEnabled();
    
    boolean isFinestEnabled();
    
    boolean isDebugEnabled();
    
    boolean isTraceEnabled();
    
    void log(Level p0, String p1, Throwable p2);
    
    void log(Level p0, String p1);
    
    void log(Level p0, String p1, Object[] p2, Throwable p3);
    
    IAgentLogger getChildLogger(Class<?> p0);
    
    IAgentLogger getChildLogger(String p0);
}

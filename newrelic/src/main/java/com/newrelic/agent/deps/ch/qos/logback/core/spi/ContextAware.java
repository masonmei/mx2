// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.status.Status;
import com.newrelic.agent.deps.ch.qos.logback.core.Context;

public interface ContextAware
{
    void setContext(Context p0);
    
    Context getContext();
    
    void addStatus(Status p0);
    
    void addInfo(String p0);
    
    void addInfo(String p0, Throwable p1);
    
    void addWarn(String p0);
    
    void addWarn(String p0, Throwable p1);
    
    void addError(String p0);
    
    void addError(String p0, Throwable p1);
}

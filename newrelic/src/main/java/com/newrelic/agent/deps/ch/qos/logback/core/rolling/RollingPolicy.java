// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import com.newrelic.agent.deps.ch.qos.logback.core.FileAppender;
import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.CompressionMode;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;

public interface RollingPolicy extends LifeCycle
{
    void rollover() throws RolloverFailure;
    
    String getActiveFileName();
    
    CompressionMode getCompressionMode();
    
    void setParent(FileAppender p0);
}

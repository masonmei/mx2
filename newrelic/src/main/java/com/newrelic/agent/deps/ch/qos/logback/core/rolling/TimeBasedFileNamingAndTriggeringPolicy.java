// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper.ArchiveRemover;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;

public interface TimeBasedFileNamingAndTriggeringPolicy<E> extends TriggeringPolicy<E>, ContextAware
{
    void setTimeBasedRollingPolicy(TimeBasedRollingPolicy<E> p0);
    
    String getElapsedPeriodsFileName();
    
    String getCurrentPeriodsFileNameWithoutCompressionSuffix();
    
    ArchiveRemover getArchiveRemover();
    
    long getCurrentTime();
    
    void setCurrentTime(long p0);
}

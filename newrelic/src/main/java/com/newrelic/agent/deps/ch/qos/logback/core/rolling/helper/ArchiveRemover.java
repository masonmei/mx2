// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling.helper;

import java.util.Date;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;

public interface ArchiveRemover extends ContextAware
{
    void clean(Date p0);
    
    void setMaxHistory(int p0);
}

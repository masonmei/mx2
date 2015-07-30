// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;

public interface Layout<E> extends ContextAware, LifeCycle
{
    String doLayout(E p0);
    
    String getFileHeader();
    
    String getPresentationHeader();
    
    String getPresentationFooter();
    
    String getFileFooter();
    
    String getContentType();
}

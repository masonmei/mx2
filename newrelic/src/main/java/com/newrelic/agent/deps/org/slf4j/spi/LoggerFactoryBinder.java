// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.spi;

import com.newrelic.agent.deps.org.slf4j.ILoggerFactory;

public interface LoggerFactoryBinder
{
    ILoggerFactory getLoggerFactory();
    
    String getLoggerFactoryClassStr();
}

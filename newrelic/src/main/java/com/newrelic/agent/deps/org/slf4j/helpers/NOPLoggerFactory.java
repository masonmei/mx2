// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.helpers;

import com.newrelic.agent.deps.org.slf4j.Logger;
import com.newrelic.agent.deps.org.slf4j.ILoggerFactory;

public class NOPLoggerFactory implements ILoggerFactory
{
    public Logger getLogger(final String name) {
        return NOPLogger.NOP_LOGGER;
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.impl;

import com.newrelic.agent.deps.ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.newrelic.agent.deps.org.slf4j.spi.MDCAdapter;

public class StaticMDCBinder
{
    public static final StaticMDCBinder SINGLETON;
    
    public MDCAdapter getMDCA() {
        return new LogbackMDCAdapter();
    }
    
    public String getMDCAdapterClassStr() {
        return LogbackMDCAdapter.class.getName();
    }
    
    static {
        SINGLETON = new StaticMDCBinder();
    }
}

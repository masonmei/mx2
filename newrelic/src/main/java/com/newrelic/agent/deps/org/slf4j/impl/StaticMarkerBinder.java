// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j.impl;

import com.newrelic.agent.deps.org.slf4j.helpers.BasicMarkerFactory;
import com.newrelic.agent.deps.org.slf4j.IMarkerFactory;
import com.newrelic.agent.deps.org.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder
{
    public static final StaticMarkerBinder SINGLETON;
    final IMarkerFactory markerFactory;
    
    private StaticMarkerBinder() {
        this.markerFactory = new BasicMarkerFactory();
    }
    
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }
    
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
    
    static {
        SINGLETON = new StaticMarkerBinder();
    }
}

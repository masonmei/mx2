// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j;

import com.newrelic.agent.deps.org.slf4j.helpers.Util;
import com.newrelic.agent.deps.org.slf4j.helpers.BasicMarkerFactory;
import com.newrelic.agent.deps.org.slf4j.impl.StaticMarkerBinder;

public class MarkerFactory
{
    static IMarkerFactory markerFactory;
    
    public static Marker getMarker(final String name) {
        return MarkerFactory.markerFactory.getMarker(name);
    }
    
    public static Marker getDetachedMarker(final String name) {
        return MarkerFactory.markerFactory.getDetachedMarker(name);
    }
    
    public static IMarkerFactory getIMarkerFactory() {
        return MarkerFactory.markerFactory;
    }
    
    static {
        try {
            MarkerFactory.markerFactory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
        }
        catch (NoClassDefFoundError e2) {
            MarkerFactory.markerFactory = new BasicMarkerFactory();
        }
        catch (Exception e) {
            Util.report("Unexpected failure while binding MarkerFactory", e);
        }
    }
}

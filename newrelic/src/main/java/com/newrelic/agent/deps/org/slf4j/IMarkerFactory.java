// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.slf4j;

public interface IMarkerFactory
{
    Marker getMarker(String p0);
    
    boolean exists(String p0);
    
    boolean detachMarker(String p0);
    
    Marker getDetachedMarker(String p0);
}

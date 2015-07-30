// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.classic.pattern;

import java.util.HashMap;
import com.newrelic.agent.deps.org.slf4j.Marker;
import com.newrelic.agent.deps.ch.qos.logback.classic.spi.ClassPackagingData;
import java.util.Map;

public class Util
{
    static Map<String, ClassPackagingData> cache;
    
    public static boolean match(final Marker marker, final Marker[] markerArray) {
        if (markerArray == null) {
            throw new IllegalArgumentException("markerArray should not be null");
        }
        for (int size = markerArray.length, i = 0; i < size; ++i) {
            if (marker.contains(markerArray[i])) {
                return true;
            }
        }
        return false;
    }
    
    static {
        Util.cache = new HashMap<String, ClassPackagingData>();
    }
}

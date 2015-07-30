// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

public enum ApdexPerfZone
{
    SATISFYING("S"), 
    TOLERATING("T"), 
    FRUSTRATING("F");
    
    private final String z;
    
    private ApdexPerfZone(final String z) {
        this.z = z;
    }
    
    public String getZone() {
        return this.z;
    }
    
    public static ApdexPerfZone getZone(final long responseTimeMillis, final long apdexTInMillis) {
        if (responseTimeMillis <= apdexTInMillis) {
            return ApdexPerfZone.SATISFYING;
        }
        if (responseTimeMillis <= 4L * apdexTInMillis) {
            return ApdexPerfZone.TOLERATING;
        }
        return ApdexPerfZone.FRUSTRATING;
    }
}

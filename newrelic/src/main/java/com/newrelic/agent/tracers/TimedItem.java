// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.tracers;

public interface TimedItem
{
    long getDurationInMilliseconds();
    
    long getDuration();
    
    long getExclusiveDuration();
}

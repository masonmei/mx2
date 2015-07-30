// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

public interface ProfilerControl
{
    void startProfiler(ProfilerParameters p0);
    
    int stopProfiler(Long p0, boolean p1);
    
    boolean isEnabled();
}

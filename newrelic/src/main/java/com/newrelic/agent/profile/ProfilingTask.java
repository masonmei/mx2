// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import com.newrelic.agent.HarvestListener;

public interface ProfilingTask extends Runnable, HarvestListener
{
    void addProfile(ProfilerParameters p0);
    
    void removeProfile(ProfilerParameters p0);
}

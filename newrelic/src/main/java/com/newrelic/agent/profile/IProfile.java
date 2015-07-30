// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.profile;

import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public interface IProfile extends JSONStreamAware
{
    void start();
    
    void end();
    
    void beforeSampling();
    
    void addStackTrace(long p0, boolean p1, ThreadType p2, StackTraceElement... p3);
    
    ProfilerParameters getProfilerParameters();
    
    int getSampleCount();
    
    Long getProfileId();
    
    ProfileTree getProfileTree(ThreadType p0);
    
    int trimBy(int p0);
    
    long getStartTimeMillis();
    
    long getEndTimeMillis();
    
    void markInstrumentedMethods();
}

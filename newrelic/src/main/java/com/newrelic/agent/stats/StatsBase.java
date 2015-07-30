// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.stats;

import com.newrelic.agent.deps.org.json.simple.JSONStreamAware;

public interface StatsBase extends Cloneable, JSONStreamAware
{
    boolean hasData();
    
    void reset();
    
    void merge(StatsBase p0);
    
    Object clone() throws CloneNotSupportedException;
}

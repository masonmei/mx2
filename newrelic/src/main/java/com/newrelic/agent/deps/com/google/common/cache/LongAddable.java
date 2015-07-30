// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.cache;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface LongAddable
{
    void increment();
    
    void add(long p0);
    
    long sum();
}

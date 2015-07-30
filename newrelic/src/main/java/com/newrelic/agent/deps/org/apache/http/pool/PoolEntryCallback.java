// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.apache.http.pool;

public interface PoolEntryCallback<T, C>
{
    void process(PoolEntry<T, C> p0);
}

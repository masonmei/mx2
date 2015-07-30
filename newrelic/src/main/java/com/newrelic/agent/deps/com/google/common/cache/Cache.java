// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.cache;

import java.util.concurrent.ConcurrentMap;
import java.util.Map;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public interface Cache<K, V>
{
    @Nullable
    V getIfPresent(Object p0);
    
    V get(K p0, Callable<? extends V> p1) throws ExecutionException;
    
    ImmutableMap<K, V> getAllPresent(Iterable<?> p0);
    
    void put(K p0, V p1);
    
    void putAll(Map<? extends K, ? extends V> p0);
    
    void invalidate(Object p0);
    
    void invalidateAll(Iterable<?> p0);
    
    void invalidateAll();
    
    long size();
    
    CacheStats stats();
    
    ConcurrentMap<K, V> asMap();
    
    void cleanUp();
}

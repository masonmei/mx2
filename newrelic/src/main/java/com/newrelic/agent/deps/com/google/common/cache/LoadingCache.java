// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.cache;

import java.util.concurrent.ConcurrentMap;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import com.newrelic.agent.deps.com.google.common.base.Function;

@Beta
@GwtCompatible
public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V>
{
    V get(K p0) throws ExecutionException;
    
    V getUnchecked(K p0);
    
    ImmutableMap<K, V> getAll(Iterable<? extends K> p0) throws ExecutionException;
    
    @Deprecated
    V apply(K p0);
    
    void refresh(K p0);
    
    ConcurrentMap<K, V> asMap();
}

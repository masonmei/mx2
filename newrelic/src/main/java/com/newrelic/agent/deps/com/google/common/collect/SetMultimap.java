// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V>
{
    Set<V> get(@Nullable K p0);
    
    Set<V> removeAll(@Nullable Object p0);
    
    Set<V> replaceValues(K p0, Iterable<? extends V> p1);
    
    Set<Map.Entry<K, V>> entries();
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(@Nullable Object p0);
}

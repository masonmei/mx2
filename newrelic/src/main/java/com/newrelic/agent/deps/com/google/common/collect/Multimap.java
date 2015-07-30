// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Multimap<K, V>
{
    int size();
    
    boolean isEmpty();
    
    boolean containsKey(@Nullable Object p0);
    
    boolean containsValue(@Nullable Object p0);
    
    boolean containsEntry(@Nullable Object p0, @Nullable Object p1);
    
    boolean put(@Nullable K p0, @Nullable V p1);
    
    boolean remove(@Nullable Object p0, @Nullable Object p1);
    
    boolean putAll(@Nullable K p0, Iterable<? extends V> p1);
    
    boolean putAll(Multimap<? extends K, ? extends V> p0);
    
    Collection<V> replaceValues(@Nullable K p0, Iterable<? extends V> p1);
    
    Collection<V> removeAll(@Nullable Object p0);
    
    void clear();
    
    Collection<V> get(@Nullable K p0);
    
    Set<K> keySet();
    
    Multiset<K> keys();
    
    Collection<V> values();
    
    Collection<Map.Entry<K, V>> entries();
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(@Nullable Object p0);
    
    int hashCode();
}

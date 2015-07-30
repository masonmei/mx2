// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface ListMultimap<K, V> extends Multimap<K, V>
{
    List<V> get(@Nullable K p0);
    
    List<V> removeAll(@Nullable Object p0);
    
    List<V> replaceValues(K p0, Iterable<? extends V> p1);
    
    Map<K, Collection<V>> asMap();
    
    boolean equals(@Nullable Object p0);
}

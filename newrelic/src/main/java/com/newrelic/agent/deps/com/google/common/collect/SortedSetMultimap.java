// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Comparator;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface SortedSetMultimap<K, V> extends SetMultimap<K, V>
{
    SortedSet<V> get(@Nullable K p0);
    
    SortedSet<V> removeAll(@Nullable Object p0);
    
    SortedSet<V> replaceValues(K p0, Iterable<? extends V> p1);
    
    Map<K, Collection<V>> asMap();
    
    Comparator<? super V> valueComparator();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.SortedMap;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface SortedMapDifference<K, V> extends MapDifference<K, V>
{
    SortedMap<K, V> entriesOnlyOnLeft();
    
    SortedMap<K, V> entriesOnlyOnRight();
    
    SortedMap<K, V> entriesInCommon();
    
    SortedMap<K, ValueDifference<V>> entriesDiffering();
}

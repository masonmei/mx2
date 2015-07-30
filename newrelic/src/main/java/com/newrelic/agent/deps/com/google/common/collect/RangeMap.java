// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Map;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface RangeMap<K extends Comparable, V>
{
    @Nullable
    V get(K p0);
    
    @Nullable
    Map.Entry<Range<K>, V> getEntry(K p0);
    
    Range<K> span();
    
    void put(Range<K> p0, V p1);
    
    void putAll(RangeMap<K, V> p0);
    
    void clear();
    
    void remove(Range<K> p0);
    
    Map<Range<K>, V> asMapOfRanges();
    
    RangeMap<K, V> subRangeMap(Range<K> p0);
    
    boolean equals(@Nullable Object p0);
    
    int hashCode();
    
    String toString();
}

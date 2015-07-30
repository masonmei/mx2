// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Table<R, C, V>
{
    boolean contains(@Nullable Object p0, @Nullable Object p1);
    
    boolean containsRow(@Nullable Object p0);
    
    boolean containsColumn(@Nullable Object p0);
    
    boolean containsValue(@Nullable Object p0);
    
    V get(@Nullable Object p0, @Nullable Object p1);
    
    boolean isEmpty();
    
    int size();
    
    boolean equals(@Nullable Object p0);
    
    int hashCode();
    
    void clear();
    
    V put(R p0, C p1, V p2);
    
    void putAll(Table<? extends R, ? extends C, ? extends V> p0);
    
    V remove(@Nullable Object p0, @Nullable Object p1);
    
    Map<C, V> row(R p0);
    
    Map<R, V> column(C p0);
    
    Set<Cell<R, C, V>> cellSet();
    
    Set<R> rowKeySet();
    
    Set<C> columnKeySet();
    
    Collection<V> values();
    
    Map<R, Map<C, V>> rowMap();
    
    Map<C, Map<R, V>> columnMap();
    
    public interface Cell<R, C, V>
    {
        R getRowKey();
        
        C getColumnKey();
        
        V getValue();
        
        boolean equals(@Nullable Object p0);
        
        int hashCode();
    }
}

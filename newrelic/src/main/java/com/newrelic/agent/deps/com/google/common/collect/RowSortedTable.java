// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
@Beta
public interface RowSortedTable<R, C, V> extends Table<R, C, V>
{
    SortedSet<R> rowKeySet();
    
    SortedMap<R, Map<C, V>> rowMap();
}

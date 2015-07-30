// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Set;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public interface BiMap<K, V> extends Map<K, V>
{
    V put(@Nullable K p0, @Nullable V p1);
    
    V forcePut(@Nullable K p0, @Nullable V p1);
    
    void putAll(Map<? extends K, ? extends V> p0);
    
    Set<V> values();
    
    BiMap<V, K> inverse();
}

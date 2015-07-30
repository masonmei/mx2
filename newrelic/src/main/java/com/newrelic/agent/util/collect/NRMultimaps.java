// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util.collect;

import com.newrelic.agent.deps.com.google.common.collect.ImmutableList;
import com.newrelic.agent.deps.com.google.common.collect.ForwardingMultimap;
import com.newrelic.agent.deps.com.google.common.collect.Multimap;
import java.util.Collection;
import com.newrelic.agent.deps.com.google.common.collect.ImmutableSet;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.collect.ForwardingSetMultimap;
import com.newrelic.agent.deps.com.google.common.collect.SetMultimap;

public class NRMultimaps
{
    public static final <K, V> SetMultimap<K, V> performantSetMultimapFrom(final SetMultimap<K, V> multimap) {
        return new ForwardingSetMultimap<K, V>() {
            public Set<V> get(final K key) {
                return (Set<V>)(this.delegate().containsKey(key) ? this.delegate().get(key) : ImmutableSet.of());
            }
            
            protected SetMultimap<K, V> delegate() {
                return multimap;
            }
        };
    }
    
    public static final <K, V> Multimap<K, V> performantMultimapFrom(final Multimap<K, V> multimap) {
        return new ForwardingMultimap<K, V>() {
            public Collection<V> get(final K key) {
                return (Collection<V>)(this.delegate().containsKey(key) ? this.delegate().get(key) : ImmutableList.of());
            }
            
            protected Multimap<K, V> delegate() {
                return multimap;
            }
        };
    }
}

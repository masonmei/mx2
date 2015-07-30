// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Map;
import com.newrelic.agent.deps.com.google.common.base.Predicate;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface FilteredMultimap<K, V> extends Multimap<K, V>
{
    Multimap<K, V> unfiltered();
    
    Predicate<? super Map.Entry<K, V>> entryPredicate();
}

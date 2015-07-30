// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import java.util.NavigableSet;
import java.util.Comparator;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public interface SortedMultiset<E> extends SortedMultisetBridge<E>, SortedIterable<E>
{
    Comparator<? super E> comparator();
    
    Multiset.Entry<E> firstEntry();
    
    Multiset.Entry<E> lastEntry();
    
    Multiset.Entry<E> pollFirstEntry();
    
    Multiset.Entry<E> pollLastEntry();
    
    NavigableSet<E> elementSet();
    
    Set<Multiset.Entry<E>> entrySet();
    
    Iterator<E> iterator();
    
    SortedMultiset<E> descendingMultiset();
    
    SortedMultiset<E> headMultiset(E p0, BoundType p1);
    
    SortedMultiset<E> subMultiset(E p0, BoundType p1, E p2, BoundType p3);
    
    SortedMultiset<E> tailMultiset(E p0, BoundType p1);
}

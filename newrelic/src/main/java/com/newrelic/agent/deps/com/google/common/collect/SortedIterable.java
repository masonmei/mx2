// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Iterator;
import java.util.Comparator;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface SortedIterable<T> extends Iterable<T>
{
    Comparator<? super T> comparator();
    
    Iterator<T> iterator();
}

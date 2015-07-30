// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import java.util.Collection;

@GwtCompatible
public interface Multiset<E> extends Collection<E>
{
    int count(@Nullable Object p0);
    
    int add(@Nullable E p0, int p1);
    
    int remove(@Nullable Object p0, int p1);
    
    int setCount(E p0, int p1);
    
    boolean setCount(E p0, int p1, int p2);
    
    Set<E> elementSet();
    
    Set<Entry<E>> entrySet();
    
    boolean equals(@Nullable Object p0);
    
    int hashCode();
    
    String toString();
    
    Iterator<E> iterator();
    
    boolean contains(@Nullable Object p0);
    
    boolean containsAll(Collection<?> p0);
    
    boolean add(E p0);
    
    boolean remove(@Nullable Object p0);
    
    boolean removeAll(Collection<?> p0);
    
    boolean retainAll(Collection<?> p0);
    
    public interface Entry<E>
    {
        E getElement();
        
        int getCount();
        
        boolean equals(Object p0);
        
        int hashCode();
        
        String toString();
    }
}

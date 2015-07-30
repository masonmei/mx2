// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Set;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface RangeSet<C extends Comparable>
{
    boolean contains(C p0);
    
    Range<C> rangeContaining(C p0);
    
    boolean encloses(Range<C> p0);
    
    boolean enclosesAll(RangeSet<C> p0);
    
    boolean isEmpty();
    
    Range<C> span();
    
    Set<Range<C>> asRanges();
    
    RangeSet<C> complement();
    
    RangeSet<C> subRangeSet(Range<C> p0);
    
    void add(Range<C> p0);
    
    void remove(Range<C> p0);
    
    void clear();
    
    void addAll(RangeSet<C> p0);
    
    void removeAll(RangeSet<C> p0);
    
    boolean equals(@Nullable Object p0);
    
    int hashCode();
    
    String toString();
}

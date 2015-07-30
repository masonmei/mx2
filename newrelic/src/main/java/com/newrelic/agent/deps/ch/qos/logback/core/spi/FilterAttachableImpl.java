// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.Filter;
import java.util.concurrent.CopyOnWriteArrayList;

public final class FilterAttachableImpl<E> implements FilterAttachable<E>
{
    CopyOnWriteArrayList<Filter<E>> filterList;
    
    public FilterAttachableImpl() {
        this.filterList = new CopyOnWriteArrayList<Filter<E>>();
    }
    
    public void addFilter(final Filter<E> newFilter) {
        this.filterList.add(newFilter);
    }
    
    public void clearAllFilters() {
        this.filterList.clear();
    }
    
    public FilterReply getFilterChainDecision(final E event) {
        for (final Filter<E> f : this.filterList) {
            final FilterReply r = f.decide(event);
            if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
                return r;
            }
        }
        return FilterReply.NEUTRAL;
    }
    
    public List<Filter<E>> getCopyOfAttachedFiltersList() {
        return new ArrayList<Filter<E>>(this.filterList);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import java.util.List;
import com.newrelic.agent.deps.ch.qos.logback.core.filter.Filter;

public interface FilterAttachable<E>
{
    void addFilter(Filter<E> p0);
    
    void clearAllFilters();
    
    List<Filter<E>> getCopyOfAttachedFiltersList();
    
    FilterReply getFilterChainDecision(E p0);
}

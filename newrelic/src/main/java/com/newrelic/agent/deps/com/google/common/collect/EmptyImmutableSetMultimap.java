// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
class EmptyImmutableSetMultimap extends ImmutableSetMultimap<Object, Object>
{
    static final EmptyImmutableSetMultimap INSTANCE;
    private static final long serialVersionUID = 0L;
    
    private EmptyImmutableSetMultimap() {
        super(ImmutableMap.<Object, ImmutableSet<Object>>of(), 0, null);
    }
    
    private Object readResolve() {
        return EmptyImmutableSetMultimap.INSTANCE;
    }
    
    static {
        INSTANCE = new EmptyImmutableSetMultimap();
    }
}

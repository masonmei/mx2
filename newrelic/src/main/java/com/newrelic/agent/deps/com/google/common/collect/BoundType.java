// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum BoundType
{
    OPEN {
        @Override
        BoundType flip() {
            return CLOSED;
        }
    }, 
    CLOSED {
        @Override
        BoundType flip() {
            return OPEN;
        }
    };
    
    static BoundType forBoolean(final boolean inclusive) {
        return inclusive ? BoundType.CLOSED : BoundType.OPEN;
    }
    
    abstract BoundType flip();
}

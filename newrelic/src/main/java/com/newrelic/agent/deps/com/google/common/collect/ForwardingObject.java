// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingObject
{
    protected abstract Object delegate();
    
    @Override
    public String toString() {
        return this.delegate().toString();
    }
}

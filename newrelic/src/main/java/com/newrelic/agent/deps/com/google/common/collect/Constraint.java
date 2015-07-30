// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
interface Constraint<E>
{
    E checkElement(E p0);
    
    String toString();
}

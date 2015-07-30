// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.collect;

import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
@Beta
public interface MapConstraint<K, V>
{
    void checkKeyValue(@Nullable K p0, @Nullable V p1);
    
    String toString();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.base;

import javax.annotation.Nullable;
import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Function<F, T>
{
    @Nullable
    T apply(@Nullable F p0);
    
    boolean equals(@Nullable Object p0);
}

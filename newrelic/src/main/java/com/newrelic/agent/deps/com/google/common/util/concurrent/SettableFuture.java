// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import javax.annotation.Nullable;

public final class SettableFuture<V> extends AbstractFuture<V>
{
    public static <V> SettableFuture<V> create() {
        return new SettableFuture<V>();
    }
    
    public boolean set(@Nullable final V value) {
        return super.set(value);
    }
    
    public boolean setException(final Throwable throwable) {
        return super.setException(throwable);
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.cache;

import com.newrelic.agent.deps.com.google.common.annotations.GwtCompatible;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public interface RemovalListener<K, V>
{
    void onRemoval(RemovalNotification<K, V> p0);
}

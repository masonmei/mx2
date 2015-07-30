// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.util.concurrent;

import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import java.util.concurrent.ScheduledFuture;

@Beta
public interface ListenableScheduledFuture<V> extends ScheduledFuture<V>, ListenableFuture<V>
{
}

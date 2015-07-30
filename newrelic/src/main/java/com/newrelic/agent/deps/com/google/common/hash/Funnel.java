// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.hash;

import com.newrelic.agent.deps.com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public interface Funnel<T> extends Serializable
{
    void funnel(T p0, PrimitiveSink p1);
}

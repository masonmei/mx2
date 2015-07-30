// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import java.io.Serializable;

public interface PreSerializationTransformer<E>
{
    Serializable transform(E p0);
}

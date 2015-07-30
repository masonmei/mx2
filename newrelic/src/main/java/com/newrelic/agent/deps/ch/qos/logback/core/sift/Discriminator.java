// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.sift;

import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;

public interface Discriminator<E> extends LifeCycle
{
    String getDiscriminatingValue(E p0);
    
    String getKey();
}

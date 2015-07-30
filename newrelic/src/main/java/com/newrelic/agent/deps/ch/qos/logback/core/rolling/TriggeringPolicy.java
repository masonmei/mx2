// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.rolling;

import java.io.File;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;

public interface TriggeringPolicy<E> extends LifeCycle
{
    boolean isTriggeringEvent(File p0, E p1);
}

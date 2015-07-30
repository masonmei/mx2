// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.OutputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.LifeCycle;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAware;

public interface Encoder<E> extends ContextAware, LifeCycle
{
    void init(OutputStream p0) throws IOException;
    
    void doEncode(E p0) throws IOException;
    
    void close() throws IOException;
}

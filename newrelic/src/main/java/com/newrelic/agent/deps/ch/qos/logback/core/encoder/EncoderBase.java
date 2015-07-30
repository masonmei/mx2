// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.encoder;

import java.io.IOException;
import java.io.OutputStream;
import com.newrelic.agent.deps.ch.qos.logback.core.spi.ContextAwareBase;

public abstract class EncoderBase<E> extends ContextAwareBase implements Encoder<E>
{
    protected boolean started;
    protected OutputStream outputStream;
    
    public void init(final OutputStream os) throws IOException {
        this.outputStream = os;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public void start() {
        this.started = true;
    }
    
    public void stop() {
        this.started = false;
    }
}

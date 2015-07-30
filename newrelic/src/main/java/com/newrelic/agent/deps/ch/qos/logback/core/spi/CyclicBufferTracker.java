// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.ch.qos.logback.core.spi;

import com.newrelic.agent.deps.ch.qos.logback.core.helpers.CyclicBuffer;

public interface CyclicBufferTracker<E>
{
    public static final int DEFAULT_BUFFER_SIZE = 256;
    public static final int DEFAULT_NUMBER_OF_BUFFERS = 64;
    public static final int THRESHOLD = 1800000;
    
    int getBufferSize();
    
    void setBufferSize(int p0);
    
    int getMaxNumberOfBuffers();
    
    void setMaxNumberOfBuffers(int p0);
    
    CyclicBuffer<E> getOrCreate(String p0, long p1);
    
    void removeBuffer(String p0);
    
    void clearStaleBuffers(long p0);
    
    int size();
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.com.google.common.io;

import java.io.IOException;
import com.newrelic.agent.deps.com.google.common.annotations.Beta;

@Beta
public interface ByteProcessor<T>
{
    boolean processBytes(byte[] p0, int p1, int p2) throws IOException;
    
    T getResult();
}

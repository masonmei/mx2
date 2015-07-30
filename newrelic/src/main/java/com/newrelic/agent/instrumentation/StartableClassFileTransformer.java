// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation;

import com.newrelic.agent.InstrumentationProxy;
import java.lang.instrument.ClassFileTransformer;

public interface StartableClassFileTransformer extends ClassFileTransformer
{
    void start(InstrumentationProxy p0, boolean p1);
}

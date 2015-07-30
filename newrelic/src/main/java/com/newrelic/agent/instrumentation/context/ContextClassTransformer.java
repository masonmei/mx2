// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import java.lang.instrument.IllegalClassFormatException;
import com.newrelic.agent.instrumentation.classmatchers.OptimizedClassMatcher;
import java.security.ProtectionDomain;

public interface ContextClassTransformer
{
    byte[] transform(ClassLoader p0, String p1, Class<?> p2, ProtectionDomain p3, byte[] p4, InstrumentationContext p5, OptimizedClassMatcher.Match p6) throws IllegalClassFormatException;
}

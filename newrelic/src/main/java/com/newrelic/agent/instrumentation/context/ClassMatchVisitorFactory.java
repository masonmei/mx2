// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.context;

import com.newrelic.agent.deps.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.deps.org.objectweb.asm.ClassReader;

public interface ClassMatchVisitorFactory
{
    ClassVisitor newClassMatchVisitor(ClassLoader p0, Class<?> p1, ClassReader p2, ClassVisitor p3, InstrumentationContext p4);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.scopedpool;

import com.newrelic.agent.deps.javassist.ClassPool;

public interface ScopedClassPoolFactory
{
    ScopedClassPool create(ClassLoader p0, ClassPool p1, ScopedClassPoolRepository p2);
    
    ScopedClassPool create(ClassPool p0, ScopedClassPoolRepository p1);
}

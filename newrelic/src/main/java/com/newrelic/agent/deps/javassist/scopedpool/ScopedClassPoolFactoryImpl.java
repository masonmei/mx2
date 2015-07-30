// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.scopedpool;

import com.newrelic.agent.deps.javassist.ClassPool;

public class ScopedClassPoolFactoryImpl implements ScopedClassPoolFactory
{
    @Override
    public ScopedClassPool create(final ClassLoader cl, final ClassPool src, final ScopedClassPoolRepository repository) {
        return new ScopedClassPool(cl, src, repository, false);
    }
    
    @Override
    public ScopedClassPool create(final ClassPool src, final ScopedClassPoolRepository repository) {
        return new ScopedClassPool(null, src, repository, true);
    }
}

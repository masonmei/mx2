// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.javassist.scopedpool;

import java.util.Map;
import com.newrelic.agent.deps.javassist.ClassPool;

public interface ScopedClassPoolRepository
{
    void setClassPoolFactory(ScopedClassPoolFactory p0);
    
    ScopedClassPoolFactory getClassPoolFactory();
    
    boolean isPrune();
    
    void setPrune(boolean p0);
    
    ScopedClassPool createScopedClassPool(ClassLoader p0, ClassPool p1);
    
    ClassPool findClassPool(ClassLoader p0);
    
    ClassPool registerClassLoader(ClassLoader p0);
    
    Map getRegisteredCLs();
    
    void clearUnregisteredClassLoaders();
    
    void unregisterClassLoader(ClassLoader p0);
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import java.io.Closeable;
import java.lang.reflect.Method;

public interface Instrumentation
{
    ExitTracer createTracer(Object p0, int p1, String p2, int p3);
    
    ExitTracer createTracer(Object p0, int p1, boolean p2, String p3, String p4, Object[] p5);
    
    Transaction getTransaction();
    
    void noticeInstrumentationError(Throwable p0, String p1);
    
    void instrument(String p0, String p1);
    
    void instrument(Method p0, String p1);
    
    void retransformUninstrumentedClass(Class<?> p0);
    
    Class<?> loadClass(ClassLoader p0, Class<?> p1) throws ClassNotFoundException;
    
    int addToObjectCache(Object p0);
    
    Object getCachedObject(int p0);
    
    boolean isWeaveClass(Class<?> p0);
    
    void registerCloseable(String p0, Closeable p1);
}

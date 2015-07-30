// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.bridge;

import java.io.Closeable;
import java.lang.reflect.Method;

public class NoOpInstrumentation implements Instrumentation
{
    public ExitTracer createTracer(final Object invocationTarget, final int signatureId, final String metricName, final int flags) {
        return null;
    }
    
    public void noticeInstrumentationError(final Throwable throwable, final String libraryName) {
    }
    
    public void instrument(final String className, final String metricPrefix) {
    }
    
    public void instrument(final Method methodToInstrument, final String metricPrefix) {
    }
    
    public void retransformUninstrumentedClass(final Class<?> classToRetransform) {
    }
    
    public Class<?> loadClass(final ClassLoader classLoader, final Class<?> theClass) throws ClassNotFoundException {
        return null;
    }
    
    public Transaction getTransaction() {
        return NoOpTransaction.INSTANCE;
    }
    
    public int addToObjectCache(final Object object) {
        return -1;
    }
    
    public Object getCachedObject(final int id) {
        return null;
    }
    
    public boolean isWeaveClass(final Class<?> clazz) {
        return false;
    }
    
    public void registerCloseable(final String string, final Closeable closeable) {
    }
    
    public ExitTracer createTracer(final Object invocationTarget, final int signatureId, final boolean dispatcher, final String metricName, final String tracerFactoryName, final Object[] args) {
        return null;
    }
}

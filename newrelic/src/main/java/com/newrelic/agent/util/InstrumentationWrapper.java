// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.jar.JarFile;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class InstrumentationWrapper implements Instrumentation
{
    protected final Instrumentation delegate;
    
    public InstrumentationWrapper(final Instrumentation delegate) {
        this.delegate = delegate;
    }
    
    public void addTransformer(final ClassFileTransformer transformer, final boolean canRetransform) {
        this.delegate.addTransformer(transformer, canRetransform);
    }
    
    public void addTransformer(final ClassFileTransformer transformer) {
        this.delegate.addTransformer(transformer);
    }
    
    public boolean removeTransformer(final ClassFileTransformer transformer) {
        return this.delegate.removeTransformer(transformer);
    }
    
    public boolean isRetransformClassesSupported() {
        return this.delegate.isRetransformClassesSupported();
    }
    
    public void retransformClasses(final Class<?>... classes) throws UnmodifiableClassException {
        if (Agent.LOG.isFinestEnabled()) {
            final StringBuilder sb = new StringBuilder("Classes about to be retransformed: ");
            for (final Class<?> current : classes) {
                sb.append(current.getName()).append(" ");
            }
            Agent.LOG.log(Level.FINEST, sb.toString());
        }
        this.delegate.retransformClasses(classes);
    }
    
    public boolean isRedefineClassesSupported() {
        return this.delegate.isRedefineClassesSupported();
    }
    
    public void redefineClasses(final ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
        if (Agent.LOG.isFinestEnabled()) {
            final StringBuilder sb = new StringBuilder("Classes about to be redefined: ");
            for (final ClassDefinition current : definitions) {
                sb.append(current.getDefinitionClass().getName()).append(" ");
            }
            Agent.LOG.log(Level.FINEST, sb.toString());
        }
        this.delegate.redefineClasses(definitions);
    }
    
    public boolean isModifiableClass(final Class<?> theClass) {
        return this.delegate.isModifiableClass(theClass);
    }
    
    public Class[] getAllLoadedClasses() {
        return this.delegate.getAllLoadedClasses();
    }
    
    public Class[] getInitiatedClasses(final ClassLoader loader) {
        return this.delegate.getInitiatedClasses(loader);
    }
    
    public long getObjectSize(final Object objectToSize) {
        return this.delegate.getObjectSize(objectToSize);
    }
    
    public void appendToBootstrapClassLoaderSearch(final JarFile jarfile) {
        this.delegate.appendToBootstrapClassLoaderSearch(jarfile);
    }
    
    public void appendToSystemClassLoaderSearch(final JarFile jarfile) {
        this.delegate.appendToSystemClassLoaderSearch(jarfile);
    }
    
    public boolean isNativeMethodPrefixSupported() {
        return this.delegate.isNativeMethodPrefixSupported();
    }
    
    public void setNativeMethodPrefix(final ClassFileTransformer transformer, final String prefix) {
        this.delegate.setNativeMethodPrefix(transformer, prefix);
    }
}

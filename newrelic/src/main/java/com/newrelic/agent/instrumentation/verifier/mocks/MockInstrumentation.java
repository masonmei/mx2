// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.instrumentation.verifier.mocks;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.instrument.ClassDefinition;
import java.util.jar.JarFile;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class MockInstrumentation implements Instrumentation
{
    public void addTransformer(final ClassFileTransformer arg0) {
    }
    
    public void addTransformer(final ClassFileTransformer arg0, final boolean arg1) {
    }
    
    public void appendToBootstrapClassLoaderSearch(final JarFile arg0) {
    }
    
    public void appendToSystemClassLoaderSearch(final JarFile arg0) {
    }
    
    public Class[] getAllLoadedClasses() {
        return new Class[0];
    }
    
    public Class[] getInitiatedClasses(final ClassLoader arg0) {
        return null;
    }
    
    public long getObjectSize(final Object arg0) {
        return 0L;
    }
    
    public boolean isModifiableClass(final Class<?> arg0) {
        return false;
    }
    
    public boolean isNativeMethodPrefixSupported() {
        return false;
    }
    
    public boolean isRedefineClassesSupported() {
        return false;
    }
    
    public boolean isRetransformClassesSupported() {
        return false;
    }
    
    public void redefineClasses(final ClassDefinition... arg0) throws ClassNotFoundException, UnmodifiableClassException {
    }
    
    public boolean removeTransformer(final ClassFileTransformer arg0) {
        return false;
    }
    
    public void retransformClasses(final Class<?>... arg0) throws UnmodifiableClassException {
    }
    
    public void setNativeMethodPrefix(final ClassFileTransformer arg0, final String arg1) {
    }
}

// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ConcurrentClassLoaders
{
    private static final Map<String, SingleClassLoader> classloaders;
    
    public static Class<?> loadClass(final ClassLoader classLoader, final String className) throws ClassNotFoundException {
        SingleClassLoader cl = ConcurrentClassLoaders.classloaders.get(className);
        if (cl == null) {
            cl = new SingleClassLoader(className);
            ConcurrentClassLoaders.classloaders.put(className, cl);
        }
        return cl.loadClass(classLoader);
    }
    
    static {
        classloaders = new ConcurrentHashMap<String, SingleClassLoader>();
    }
}

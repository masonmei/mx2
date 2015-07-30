// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SingleClassLoader
{
    static final int DEFAULT_MAX_SIZE = 50;
    private Map<ClassLoader, Class<?>> classMap;
    private final String className;
    private final int maxSize;
    
    public SingleClassLoader(final String className) {
        this(className, 50);
    }
    
    public SingleClassLoader(final String className, final int maxSize) {
        this.classMap = new ConcurrentHashMap<ClassLoader, Class<?>>();
        this.className = className;
        this.maxSize = maxSize;
    }
    
    public Class<?> loadClass(final ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz = this.classMap.get(classLoader);
        if (clazz == null) {
            clazz = classLoader.loadClass(this.className);
            if (this.classMap.size() == this.maxSize) {
                this.classMap.clear();
            }
            this.classMap.put(classLoader, clazz);
        }
        return clazz;
    }
    
    public void clear() {
        this.classMap.clear();
    }
    
    int getSize() {
        return this.classMap.size();
    }
}

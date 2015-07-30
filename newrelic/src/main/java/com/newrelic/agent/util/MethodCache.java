// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.util;

import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;
import java.util.Map;

public class MethodCache
{
    static final int DEFAULT_MAX_SIZE = 100;
    private final Map<Class<?>, Method> methods;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final int maxSize;
    
    public MethodCache(final String methodName, final Class<?>... parameterTypes) {
        this(100, methodName, parameterTypes);
    }
    
    public MethodCache(final int maxSize, final String methodName, final Class<?>... parameterTypes) {
        this.methods = new ConcurrentHashMap<Class<?>, Method>();
        this.maxSize = maxSize;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }
    
    public Method getDeclaredMethod(final Class<?> clazz) throws NoSuchMethodException {
        Method method = this.methods.get(clazz);
        if (method == null) {
            method = clazz.getDeclaredMethod(this.methodName, this.parameterTypes);
            method.setAccessible(true);
            if (this.methods.size() == this.maxSize) {
                this.methods.clear();
            }
            this.methods.put(clazz, method);
        }
        return method;
    }
    
    public Method getDeclaredMethod(final Class<?> clazz, final Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = this.methods.get(clazz);
        if (method == null) {
            method = clazz.getDeclaredMethod(this.methodName, parameterTypes);
            method.setAccessible(true);
            if (this.methods.size() == this.maxSize) {
                this.methods.clear();
            }
            this.methods.put(clazz, method);
        }
        return method;
    }
    
    public Method getMethod(final Class<?> clazz) throws NoSuchMethodException {
        Method method = this.methods.get(clazz);
        if (method == null) {
            method = clazz.getMethod(this.methodName, this.parameterTypes);
            if (this.methods.size() == this.maxSize) {
                this.methods.clear();
            }
            this.methods.put(clazz, method);
        }
        return method;
    }
    
    public void clear() {
        this.methods.clear();
    }
    
    int getSize() {
        return this.methods.size();
    }
}
